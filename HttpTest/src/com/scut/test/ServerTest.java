package com.scut.test;

import java.io.IOException;
import java.nio.charset.Charset;

import com.scut.server.Reactor;

public class ServerTest {

	public static void main(String[] args) throws IOException {	
		
		Reactor r = new Reactor(9988, "src\\NewFile.xml");
		r.run();
		
	}

}
