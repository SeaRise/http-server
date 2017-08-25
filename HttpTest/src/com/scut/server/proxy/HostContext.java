package com.scut.server.proxy;

public class HostContext {
	
    private String[] hosts = {"127.0.0.1:9988", "127.0.0.1:9999"};
	
	private int pointer = -1;
	
	public String getHost() {
		if (pointer == hosts.length - 1) {
			pointer = 0;
		} else {
			pointer++;
		}
		
		return hosts[pointer];
	}
}
