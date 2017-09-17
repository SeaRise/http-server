package com.scut.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import com.scut.server.pool.ThreadPool;

public class Reactor implements Runnable {
	
	private final int POOL_NUM = 5;
	
	private Selector selector = null;
	private ServerSocketChannel serverSocket = null;
	private ChannelContext channelContext = null;
	private XmlScanner scanner = null;
	private ThreadPool pool = null;
	
	public Reactor(int port, String xmlPath) {
		try {
			selector = Selector.open();
			serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind(
					new InetSocketAddress(port));
			serverSocket.configureBlocking(false);
			serverSocket.register(selector, SelectionKey.OP_ACCEPT, new Acceptor());
		} catch (IOException e) {
			
			//close();
			e.printStackTrace();
		}
		channelContext = new ChannelContext();
		scanner = new XmlScanner(xmlPath, channelContext);
		this.pool = ThreadPool.getThreadPool(POOL_NUM);
	}
	
	public void close() {
		scanner.close();
		selector = null;
		serverSocket = null;
		channelContext = null;
		scanner = null;
	}
	
	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				selector.select(100);
				
				Set<SelectionKey> selected = selector.selectedKeys();
				Iterator<SelectionKey> it = selected.iterator();
				while (it.hasNext()) {
					dispatch((SelectionKey) it.next());
					it.remove();
				}
			}		
		} catch (IOException e) {
			close();
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

				if (c != null) {
					new Handler(selector, c, channelContext, pool, scanner);
				}
				
			} catch (IOException e) {
				close();
				e.printStackTrace();
			}
		}
		
	}
	
}
