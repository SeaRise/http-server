package com.scut.server;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;

import com.scut.server.parser.http.HttpParser;
import com.scut.server.serlvet.ChannelInf;
import com.scut.server.serlvet.ChannerHandler;
import com.scut.server.serlvet.HttpRequest;
import com.scut.server.serlvet.HttpResponse;

public class Handler implements Runnable {
	
	//private int num;
	
	private Selector selector = null;
	private SelectionKey sk = null;
	
	private HttpParser parser = null;
	
	//错误访问的标志
	private boolean isError = false;
	
	static final int READING = 1;
	static final int SENDING = 0;
	static final int STOPING = 3;
	
	private int state = READING;
	
	static final int BUFFER_CAPACITY = 4096;
	
	private ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
	
	private ChannelInf channelInf = null;
	
	private LinkedList<String> writeMessQueue = null;
	
	private ChannerHandler channerHandler = null;
	
	private ChannelContext channelContext = null;
	
	private XmlScanner scanner = null;
	
	Handler(Selector selector, SocketChannel socket, 
			ChannelContext channelContext,
			XmlScanner scanner) {
		try {
			socket.configureBlocking(false);
			sk = socket.register(selector, 
					SelectionKey.OP_READ, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		
		this.selector = selector;
		this.channelContext = channelContext;
		this.scanner = scanner;
		
		writeMessQueue = new LinkedList<String>();
		parser = new HttpParser();
		//this.num = num;
		//active();
		//selector.wakeup();
	}
	
	
	@Override
	public void run() {
		//System.out.println("run");
		//System.out.println(num);
		try {	
			if (state == READING) {
				read();
			}
			
			if (state == SENDING) {
				write();
			}	
			hasSomethingSend();
		} catch (Exception e) {
			//e.printStackTrace();
			close();
		}
	}
	
	private void hasSomethingSend() {
		//System.out.println("hasSomethingSend");
		//System.out.println(num);
		//System.out.println(writeMessQueue.size());
		if (!writeMessQueue.isEmpty()) {
			sk.interestOps(SelectionKey.OP_WRITE);
			state = SENDING;
		}
		
		else {
			//http取消持久化
			if (!parser.getValue("Connection").equals("keep-alive")) {
				close();
			}
			
			sk.interestOps(SelectionKey.OP_READ);
			state = READING;
		}
	}
	
	private void read() throws IOException {
		//System.out.println("read");
		//System.out.println(num);
		SocketChannel clientChannel = (
				SocketChannel) sk.channel();
		int count ;
		buf.clear();
		parser.clear();
		StringBuffer sb = new StringBuffer();
		String line = null;
		
		Socket s = clientChannel.socket();
		
		
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
        }
		
		String uri = parser.getValue("Uri");
		//System.out.println(uri);
		
		if (uri.equals("/favicon.ico")) {
			return;
		}
		
		channerHandler = scanner.getServlet(
				uri.substring(1, uri.length()));
		//System.out.println(uri.substring(1, uri.length()));
		if (channerHandler == null) {
			writeAndFlush("HTTP/1.1 404 Not found\r\n" +
					      "Content-Length:13" + 
					      "\r\n\r\n404 Not found\r\n");
			isError = true;
			return;
		}
		
		//System.out.println("not null");
		HttpRequest request = parser.getHttpRequest(channelInf);
		HttpResponse response = parser.getHttpResponse(channelInf);
		
		channerHandler.service(request, response, channelContext);
		
	}
	
	public void writeAndFlush(String mess) {
		//System.out.println("writeAndFlush");
		//System.out.println(num);
		writeMessQueue.addLast(mess);
		hasSomethingSend();
		//System.out.println(writeMessQueue.size());
	}
	
	private void write() throws IOException {
		if (channerHandler == null && !isError) {
			return;
		}
		//System.out.println("write");
		//System.out.println(num);
		SocketChannel clientChannel = (
				SocketChannel) sk.channel();
		buf.clear();
		String mess = writeMessQueue.removeFirst();
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
		//System.out.println(writeMess);
		buf.put(writeMess.getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			clientChannel.write(buf);
		}	
		
		//可能有bug
		channerHandler = null;
	}
	
	void close() {
		sk.cancel();
		try {
			sk.channel().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		selector = null;
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
