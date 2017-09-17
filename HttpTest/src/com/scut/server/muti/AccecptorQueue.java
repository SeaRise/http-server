package com.scut.server.muti;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AccecptorQueue extends ConcurrentLinkedQueue<SocketChannel> {
	
	private static AccecptorQueue queue = new AccecptorQueue();
	
	public static AccecptorQueue getAccecptorQueue() {
		return queue;
	}
	
}
