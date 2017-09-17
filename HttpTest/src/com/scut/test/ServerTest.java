package com.scut.test;

import java.io.IOException;
import java.nio.charset.Charset;

import com.scut.server.Reactor;
import com.scut.server.muti.MainReactor;
import com.scut.server.muti.SubReactor;

public class ServerTest {

	public static void main(String[] args) throws IOException {	
		
		/*
		Reactor r = new Reactor(9988, "src\\NewFile.xml");
		r.run();
		*/
		
		new Thread(new MainReactor(9988)).start();
		new Thread(new SubReactor("src\\NewFile.xml")).start();
		
	}

}
