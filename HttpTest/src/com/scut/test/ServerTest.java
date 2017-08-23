package com.scut.test;

import com.scut.server.Reactor;

public class ServerTest {

	public static void main(String[] args) {	
		Reactor r = new Reactor(9988, "F:\\WareHouse\\http-server\\HttpTest\\src\\NewFile.xml");
		r.run();
	}

}
