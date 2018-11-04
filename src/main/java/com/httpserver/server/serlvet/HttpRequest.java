package com.httpserver.server.serlvet;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
	
	private Map<String, String> attrMap;
	
	private ChannelInf channelInf = null;
	
	private String method;
	
	public HttpRequest(ChannelInf channelInf, String body) {
		this.channelInf = channelInf;
		setAttr(body);
	}
	
	public String getMethod() {
		return method;
	}
	
	private void setAttr(String body) {
		if (body == null) {
			method = "GET";
			return;
		}
		
		method = "POST";
		attrMap = new HashMap<String, String>();
		String[] KeyAndValues = body.split("&");
		
		for (int i = 0; i < KeyAndValues.length; i++) {
			String[] KeyAndValue = KeyAndValues[i].split("=");
			attrMap.put(KeyAndValue[0], KeyAndValue[1]);
		}
	}
	
	public String getParameter(String name) {
		return attrMap.get(name);
	}
	
	public String getRemoteAddr() {
		return channelInf.getRemoteIP();
	}
	
	public int getRemotePort() {
		return channelInf.getRemotePort();
	}
	
	public String getLocalAddr() {
		return channelInf.getLocalIP();
	}
	
    public int getLocalPort() {
		return channelInf.getLocalPort();
	}
}
