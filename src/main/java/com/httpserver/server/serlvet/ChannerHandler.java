package com.httpserver.server.serlvet;

import com.httpserver.server.reactor.ChannelContext;

public class ChannerHandler {
	
    public void service(HttpRequest request,
    		HttpResponse response, 
    		ChannelContext channelContext) {
    	if (request.getMethod().equals("GET")) {
    		doGet(request, response, channelContext);
    	}
    	if (request.getMethod().equals("POST")) {
    		doPost(request, response, channelContext);
    	}
    }
    
    public void init(ChannelContext channelContext) {
    	
    }
    
    public void destroy(ChannelContext channelContext) {
    	
    }
    
    public void doPost(HttpRequest request,
    		HttpResponse response, 
    		ChannelContext channelContext) {
    	
    }
    
    public void doGet(HttpRequest request,
    		HttpResponse response, 
    		ChannelContext channelContext) {
    	
    }

}
