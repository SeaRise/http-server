package com.scut.server.muti;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.scut.server.pool.ThreadPool;

public class MainReactor implements Runnable {

	private Selector selector = null;
	private ServerSocketChannel serverSocket = null;

	public MainReactor(int port) {
		try {
			selector = Selector.open();
			serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind(
					new InetSocketAddress(port));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				selector.select(100);
				//System.out.println("select");
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> it = selected.iterator();
				while (it.hasNext()) {
					dispatch((SelectionKey) it.next());
					it.remove();
				}
				Thread.yield();
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void dispatch(SelectionKey k) {
		Runnable r = (Runnable)(k.attachment());
		if (r != null) {
			r.run();
		}
	}
	
	class Acceptor implements Runnable {

		@Override
		public void run() {
			try {
				SocketChannel c = serverSocket.accept();
				//System.out.println("accept");
				if (c != null) {
					
					AccecptorQueue.getAccecptorQueue().add(c);

				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
