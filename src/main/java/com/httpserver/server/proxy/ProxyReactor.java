package com.httpserver.server.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ProxyReactor implements Runnable {
	//static int num = 0;
	
		private Selector selector = null;
		private ServerSocketChannel serverSocket = null;
		private HostContext channelContext = null;
		
		public ProxyReactor(int port) {
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
			channelContext = new HostContext();
		}
		
		public void run() {
			try {
				while (!Thread.interrupted()) {
					selector.select();
					//System.out.println("select");
					Set<SelectionKey> selected = selector.selectedKeys();
					Iterator<SelectionKey> it = selected.iterator();
					while (it.hasNext()) {
						dispatch((SelectionKey) it.next());
						it.remove();
					}
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

			public void run() {
				try {
					SocketChannel c = serverSocket.accept();
					//System.out.println("accept");
					if (c != null) {
						//num++;
						//new Handler(selector, c, channerHandler, channelContext, num);
						new ProxyHandler(selector, c, channelContext);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
}
