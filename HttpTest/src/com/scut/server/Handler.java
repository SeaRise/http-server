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
	
	static final int READING = 1;
	static final int SENDING = 0;
	static final int STOPING = 3;
	
	private int state = READING;
	
	static final int BUFFER_CAPACITY = 4096;
	
	private ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
	
	private ChannelInf channelInf;
	
	private LinkedList<String> writeMessQueue = null;
	
	private ChannerHandler channerHandler;
	
	private ChannelContext channelContext;
	
	Handler(Selector selector, SocketChannel socket, 
			ChannerHandler channerHandler, 
			ChannelContext channelContext) {
		this.selector = selector;
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
		this.channerHandler = channerHandler;
		this.channelContext = channelContext;
		writeMessQueue = new LinkedList<String>();
		parser = new HttpParser();
		//this.num = num;
		active();
		//selector.wakeup();
	}
	
	
	@Override
	public void run() {
		//System.out.println("run");
		//System.out.println(num);
		try {
			if (channerHandler != null) {
				
				if (state == READING) {
					read();
				}
				
				if (state == SENDING) {
					write();
				}
				
				hasSomethingSend();
			}	
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
		
		HttpRequest request = parser.getHttpRequest(channelInf);
		HttpResponse response = parser.getHttpResponse(channelInf);
		
		channerHandler.channelService(request, response, channelContext);
		
	}
	
	public void writeAndFlush(String mess) {
		//System.out.println("writeAndFlush");
		//System.out.println(num);
		writeMessQueue.addLast(mess);
		hasSomethingSend();
		//System.out.println(writeMessQueue.size());
	}
	
	private void active() {
		//System.out.println("active");
		//System.out.println(num);
		channerHandler.channelActive(
				channelInf, channelContext);
		hasSomethingSend();
	}
	
	private void write() throws IOException {
		//System.out.println("write");
		//System.out.println(num);
		SocketChannel clientChannel = (
				SocketChannel) sk.channel();
		buf.clear();
		String mess = writeMessQueue.removeFirst();
		String writeMess = "HTTP/1.1 200 OK\r\n" + 
				"Content-Type:text/html; charset=UTF-8\r\n" + 
				"Content-Length:" + mess.length() + "\r\n\r\n" + 
				mess;
		buf.put(writeMess.getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			clientChannel.write(buf);
		}		
	}
	
	void close() {
		channerHandler.channelDisconnect(
				channelInf, channelContext);
		sk.cancel();
	}
}
