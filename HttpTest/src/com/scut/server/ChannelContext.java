package com.scut.server;

import java.util.HashMap;

public class ChannelContext {
	
	HashMap<String, Object> ContextMap;
	
	ChannelContext() {
		ContextMap = new HashMap<String, Object>();
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
