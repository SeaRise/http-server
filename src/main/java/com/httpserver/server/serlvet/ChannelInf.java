package com.httpserver.server.serlvet;

import com.httpserver.server.reactor.Handler;

public class ChannelInf {
	private String myIP;
	private String yourIP;
	
	private int myPort;
	private int yourPort;
	
	private Handler handler;
	
	public ChannelInf(String myIP, String yourIP, 
			int myPort, int yourPort, Handler handler) {
		this.myIP = myIP;
		this.yourIP = yourIP;
		this.myPort = myPort;
		this.yourPort = yourPort;
		this.handler = handler;
	}
	
	public String getLocalIP() {
		return myIP;
	}
	
	public String getRemoteIP() {
		return yourIP;
	}
	
	public int getLocalPort() {
		return myPort;
	}
	
	public int getRemotePort() {
		return yourPort;
	}
	
	public void writeAndFlush(String mess) {
		handler.writeAndFlush(mess);
	}
}
