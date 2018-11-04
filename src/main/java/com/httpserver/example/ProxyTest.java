package com.httpserver.example;

import com.httpserver.server.proxy.ProxyReactor;

public class ProxyTest {

	public static void main(String[] args) {
		ProxyReactor r = new ProxyReactor(9977);
		r.run();
	}

}
