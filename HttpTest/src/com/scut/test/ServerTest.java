package com.scut.test;

import com.scut.server.ChannelContext;
import com.scut.server.Reactor;
import com.scut.server.serlvet.ChannelInf;
import com.scut.server.serlvet.ChannerHandler;
import com.scut.server.serlvet.HttpRequest;
import com.scut.server.serlvet.HttpResponse;

public class ServerTest {

	public static void main(String[] args) {
		Reactor r = new Reactor(9988, new ChannerHandler() {
			@Override
			public void channelService(HttpRequest request,
		    		HttpResponse response, 
					ChannelContext channelContext) {
				response.writeAndFlush("<html><body><h1>fds打撒</h1><p>fdfdfd</p></body></html>");
			}

			@Override
			public void channelActive(ChannelInf channelInf,
					ChannelContext channelContext) {
			}

			@Override
			public void channelDisconnect(ChannelInf channelInf,
					ChannelContext channelContext) {
				System.out.println("disconnect");
			}
			
		});
		r.run();
	}

}
