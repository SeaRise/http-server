package com.httpserver.server.serlvet;


public class HttpResponse {
	
	private ChannelInf channelInf = null;
	
	public HttpResponse(ChannelInf channelInf) {
		this.channelInf = channelInf;
	}
	
	public void writeAndFlush(String mess) {
		channelInf.writeAndFlush(mess);
	}
}
