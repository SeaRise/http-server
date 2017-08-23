package com.scut.server.serlvet;

import com.scut.server.ChannelContext;

public class ChannerHandler {
	
    public void channelService(HttpRequest request,
    		HttpResponse response, 
    		ChannelContext channelContext) {
    	if (request.getMethod().equals("GET")) {
    		doGet(request, response, channelContext);
    	}
    	if (request.getMethod().equals("POST")) {
    		doPost(request, response, channelContext);
    	}
    }
    
    public void channelActive(ChannelInf channelInf, 
    		ChannelContext channelContext) {
    	
    }
    
    public void channelDisconnect(ChannelInf channelInf, 
    		ChannelContext channelContext) {
    	
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
