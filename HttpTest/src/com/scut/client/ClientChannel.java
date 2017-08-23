package com.scut.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class ClientChannel {
	
    private SocketChannel socketChannel;
	
    static final int BUFFER_CAPACITY = 4096;
	private ByteBuffer buf = ByteBuffer.allocate(BUFFER_CAPACITY);
	
	public ClientChannel () {
		try {
			socketChannel = SocketChannel.open();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(String romteIP, int port) {
		try {
			socketChannel.connect(new InetSocketAddress(romteIP, port));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String read(){
		byte[] bytes = null;
		try {
			buf.clear();
			socketChannel.read(buf);
			buf.flip();
            bytes = new byte[buf.remaining()];
            buf.get(bytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(bytes);
	}
	
	public void send(String mess) {
		buf.clear();
		buf.put(mess.getBytes());
		buf.flip();
		while(buf.hasRemaining()) {
			try {
				socketChannel.write(buf);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void close() {
		buf = null;
		try {
			socketChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
