package com.scut.test;

import java.io.UnsupportedEncodingException;

import com.scut.server.ChannelContext;
import com.scut.server.serlvet.ChannerHandler;
import com.scut.server.serlvet.HttpRequest;
import com.scut.server.serlvet.HttpResponse;

public class TestServlet extends ChannerHandler{
	
	@Override
	public void doPost(HttpRequest request, HttpResponse response,
			ChannelContext channelContext) {
		System.out.println("post1");
		response.writeAndFlush("<html><body><h1>" + 
		                       request.getParameter("word") + "\r\n" +  
		                       request.getParameter("hello") + 
				               "</h1></body></html>");
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response,
			ChannelContext channelContext) {
		// TODO Auto-generated method stub
		System.out.println("get1");
		response.writeAndFlush("<html><body><h1>我的第一个标题</h1><p>我的第一个段落。</p></body></html>");
	}
	
	@Override
	public void init(ChannelContext channelContext) {
		// TODO Auto-generated method stub
		super.init(channelContext);
		System.out.println("init1");
	}

	@Override
	public void destroy(ChannelContext channelContext) {
		// TODO Auto-generated method stub
		super.destroy(channelContext);
		System.out.println("des1");
	}
	
}
