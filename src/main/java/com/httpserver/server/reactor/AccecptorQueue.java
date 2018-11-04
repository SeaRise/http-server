package com.httpserver.server.reactor;

import java.nio.channels.SocketChannel;
import java.util.concurrent.ConcurrentLinkedQueue;

@SuppressWarnings("serial")
public class AccecptorQueue extends ConcurrentLinkedQueue<SocketChannel> {
	
	private static AccecptorQueue queue = new AccecptorQueue();
	
	public static AccecptorQueue getAccecptorQueue() {
		return queue;
	}
	
}
