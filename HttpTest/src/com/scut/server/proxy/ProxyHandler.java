package com.scut.server.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.scut.server.parser.http.HttpParser;

public class ProxyHandler implements Runnable {
	
		private  SelectionKey sk = null;
		
		static final int READING = 1;
		static final int SENDING = 0;
		
		private HttpParser parser = null;
		
		private int state = READING;
		
		static final int BUFFER_CAPACITY = 4096;
		
		private ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
		
		private String writeMess = null;
		
		private HostContext channelContext;
		
		ProxyHandler(Selector selector, SocketChannel socket, 
				HostContext channelContext) {
			try {
				socket.configureBlocking(false);
				sk = socket.register(selector, 
						SelectionKey.OP_READ, this);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			SocketChannel clientChannel = (SocketChannel) sk.channel();
			this.channelContext = channelContext;
			parser = new HttpParser();
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
				
			} catch (Exception e) {
				close();
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

			writeMess = "HTTP/1.1 302 Moved Temporarily\r\n" +
	                    "Location: " + "http://" + 
					    channelContext.getHost() + 
					    parser.getValue("Uri") + 
	                    "\r\n\r\n";

			sk.interestOps(SelectionKey.OP_WRITE);
			state = SENDING;
		}
		
		private void write() throws IOException {
			//System.out.println("write");
			//System.out.println(num);
			SocketChannel clientChannel = (
					SocketChannel) sk.channel();
			buf.clear();
			buf.put(writeMess.getBytes());
			buf.flip();
			while(buf.hasRemaining()) {
				clientChannel.write(buf);
			}		
			close();
		}
		
		void close() {
			try {
				sk.channel().close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
}
