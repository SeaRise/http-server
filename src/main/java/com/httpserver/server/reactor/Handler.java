package com.httpserver.server.reactor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.httpserver.server.parser.http.HttpParser;
import com.httpserver.server.pool.ThreadPool;
import com.httpserver.server.serlvet.ChannelInf;
import com.httpserver.server.serlvet.ChannerHandler;
import com.httpserver.server.serlvet.HttpRequest;
import com.httpserver.server.serlvet.HttpResponse;

public class Handler implements Runnable {
	
	private SelectionKey sk = null;
	
	private HttpParser parser = null;
	
	//错误访问的标志
	private boolean isError = false;
	
	//访问静态资源的标志
	private boolean isStatic = false;
	
	//访问serlvet的标志
	private volatile boolean isServlet = false;
	
	static final int READING = 1;
	static final int SENDING = 0;
	static final int STOPING = 3;
	
	private int state = READING;
	
	static final int BUFFER_CAPACITY = 4096;
	
	private ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
	
	private ChannelInf channelInf = null;
	
	private ConcurrentLinkedQueue<String> writeMessQueue = null;
	
	private ChannerHandler channerHandler = null;
	
	private ChannelContext channelContext = null;
	
	private XmlScanner scanner = null;
	
	private ThreadPool pool = null;
	
	public Handler(Selector selector, SocketChannel socket, 
			ChannelContext channelContext,
			ThreadPool pool, XmlScanner scanner) {
		try {
			socket.configureBlocking(false);
			sk = socket.register(selector, 
					SelectionKey.OP_READ, this);
		} catch (IOException e) {
			e.printStackTrace();
		}
		SocketChannel clientChannel = (SocketChannel) sk.channel();
		channelInf = new ChannelInf(
				clientChannel.socket().
				getLocalAddress().getHostAddress(), 
				clientChannel.socket().
				getInetAddress().getHostAddress(),
				clientChannel.socket().getLocalPort(),
				clientChannel.socket().getPort(),
				this);
		
		this.channelContext = channelContext;
		this.scanner = scanner;
		this.pool = pool;
		
		writeMessQueue = new ConcurrentLinkedQueue<String>();
		parser = new HttpParser();
	}
	
	
	public void run() {
		try {	
			if (state == READING) {
				read();
			}
			
			if (state == SENDING) {
				write();
			}	
			hasSomethingSend();
		} catch (Exception e) {
			close();
		}
	}
	
	private void hasSomethingSend() {
		if (!writeMessQueue.isEmpty()) {
			sk.interestOps(SelectionKey.OP_WRITE);
			state = SENDING;
		}
		
		else {
			//http取消持久化
			if (!parser.getValue("Connection").equals("keep-alive")) {
				close();
			} else {
				sk.interestOps(SelectionKey.OP_READ);
				state = READING;
			}
		}
	}
	
	private void read() throws IOException {
		SocketChannel clientChannel = (
				SocketChannel) sk.channel();
		int count ;
		buf.clear();
		parser.clear();
		
		while ((count = clientChannel.read(buf)) > 0) {
			buf.flip();
            byte[] bytes = new byte[buf.remaining()];
            buf.get(bytes);
            
            BufferedReader reader = new BufferedReader(
            		new InputStreamReader(new ByteArrayInputStream(bytes)));
            parser.parse(reader);
        }
		if (count < 0) {
		    sk.cancel();
		    return;
        }
		
		String uri = parser.getValue("Uri");
		
		if (uri.equals("/favicon.ico")) {
			return;
		}
		
		String path = constructPath(uri.substring(1, uri.length()));
		
		if ((isStatic = getStaticFile(path)) == true) {
			return;
		}
		
		channerHandler = scanner.getServlet(path);
		if (channerHandler == null) {
			writeAndFlush("HTTP/1.1 404 Not found\r\n" +
					      "Content-Length:13" + 
					      "\r\n\r\n404 Not found\r\n");
			isError = true;
			return;
		}
		
		HttpRequest request = parser.getHttpRequest(channelInf);
		HttpResponse response = parser.getHttpResponse(channelInf);
		
		pool.execute(new workerThread(request, response, channerHandler));
	}
	
    class workerThread implements Runnable {
		
    	HttpRequest request = null;
		HttpResponse response = null;
		ChannerHandler handler = null;
		
		workerThread (HttpRequest request, HttpResponse response, 
				ChannerHandler channerHandler) {
			this.request = request;
			this.response = response;
			this.handler = channerHandler;
		}
		
		public void run() {
			handler.service(request, response, channelContext);
			isServlet = true;
		}
	}
	
	private String constructPath(String path) {
		String[] elements = path.split("/");
		StringBuffer realPath = new StringBuffer();
		
		int i;
		for (i = 0; i < elements.length - 1; i++) {
			realPath.append(elements[i] + File.separator);
		}
		realPath.append(elements[i]);
		
		return realPath.toString();
	}
	
	//判断静态资源在否,且发送
	private boolean getStaticFile(String filePath) throws IOException {
		File fileToSend = new File("src\\main\\java\\com\\httpserver\\example\\" + filePath);
		
		//没有文件或为目录
		if (!(fileToSend.exists() && !fileToSend.isDirectory())) {
			return false;
		}

		FileInputStream fis = new FileInputStream(fileToSend);
        byte[] buf = new byte[fis.available()];
        fis.read(buf);

        writeAndFlush(new String(buf));
        
        fis.close();
		return true;
	}
	
	public void writeAndFlush(String mess) {
		writeMessQueue.add(mess);
		hasSomethingSend();
	}
	
	private void write() throws IOException {
		if (!isServlet && !isError && !isStatic) {
			return;
		}
		SocketChannel clientChannel = (
				SocketChannel) sk.channel();
		buf.clear();
		String mess = writeMessQueue.poll();
		String writeMess;
		if (isError) {
			writeMess = mess;
			isError = false;
		} else {
			writeMess = "HTTP/1.1 200 OK\r\n" + 
					"Content-Type:text/html; charset=UTF-8\r\n" + 
					"Content-Length:" + (mess.getBytes().length) + "\r\n\r\n" + 
					mess + "\r\n";
		}
		buf.put(writeMess.getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			clientChannel.write(buf);
		}	
		
		//可能有bug
		isServlet = false;
		isStatic = false;
	}
	
	void close() {
		
		sk.cancel();
		try {
			sk.channel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		sk = null;
		parser = null;
		buf = null;
		channelInf = null;
		writeMessQueue = null;
        channerHandler = null;
		channelContext = null;
		scanner = null;
	}
}
