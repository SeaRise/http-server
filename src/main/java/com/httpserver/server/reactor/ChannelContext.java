package com.httpserver.server.reactor;

import java.util.concurrent.ConcurrentHashMap;

public class ChannelContext {
	
	ConcurrentHashMap<String, Object> ContextMap;
	
	public ChannelContext() {
		ContextMap = new ConcurrentHashMap<String, Object>();
	}
	
	public void put(String name, Object element) {
		ContextMap.put(name, element);
	}
	
	public boolean hasName(String name) {
		return ContextMap.containsKey(name);
	}
	
	public Object get(String name) {
		return ContextMap.get(name);
	}
	
}
