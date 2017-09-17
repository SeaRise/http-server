package com.scut.server.muti;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Set;

import com.scut.server.ChannelContext;
import com.scut.server.Handler;
import com.scut.server.XmlScanner;
import com.scut.server.pool.ThreadPool;

public class SubReactor implements Runnable {
	
	private final int POOL_NUM = 5;
	
	private Selector selector = null;
	private ChannelContext channelContext = null;
	private XmlScanner scanner = null;
	
	private ThreadPool pool = null;
	
	public SubReactor(String xmlPath) {
		
		try {
			selector = Selector.open();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		channelContext = new ChannelContext();
		scanner = new XmlScanner(xmlPath, channelContext);
		this.pool = ThreadPool.getThreadPool(POOL_NUM);
	}

	@Override
	public void run() {
		try {
			while (!Thread.interrupted()) {
				
				if (!AccecptorQueue.getAccecptorQueue().isEmpty()) {
					new Handler(selector, AccecptorQueue.getAccecptorQueue().poll(), 
							channelContext, pool, scanner);
				}
				
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
	
}
