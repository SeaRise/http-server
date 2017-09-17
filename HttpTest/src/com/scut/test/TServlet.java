package com.scut.test;

import com.scut.server.ChannelContext;
import com.scut.server.serlvet.ChannerHandler;
import com.scut.server.serlvet.HttpRequest;
import com.scut.server.serlvet.HttpResponse;

public class TServlet extends ChannerHandler{

	@Override
	public void doPost(HttpRequest request, HttpResponse response,
			ChannelContext channelContext) {
		// TODO Auto-generated method stub
		System.out.println("post2");
	}

	@Override
	public void doGet(HttpRequest request, HttpResponse response,
			ChannelContext channelContext) {
		// TODO Auto-generated method stub
		System.out.println("get2");
		response.writeAndFlush("<html><body><h1>我的第2个标题</h1><p>我的第2个段落。</p></body></html>");
	}

	@Override
	public void init(ChannelContext channelContext) {
		// TODO Auto-generated method stub
		super.init(channelContext);
		System.out.println("init2");
	}

	@Override
	public void destroy(ChannelContext channelContext) {
		// TODO Auto-generated method stub
		super.destroy(channelContext);
		System.out.println("des2");
	}
	
}
