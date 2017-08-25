package com.scut.test;

import com.scut.server.proxy.ProxyReactor;

public class ProxyTest {

	public static void main(String[] args) {
		ProxyReactor r = new ProxyReactor(9977);
		r.run();
	}

}
