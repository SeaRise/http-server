package com.httpserver.example;

import java.io.IOException;

import com.httpserver.server.reactor.MainReactor;
import com.httpserver.server.reactor.SubReactor;

public class ServerTest {

	public static void main(String[] args) throws IOException {	
		new Thread(new MainReactor(9988)).start();
		new Thread(new SubReactor("src\\main\\java\\com\\httpserver\\example\\NewFile.xml")).start();
	}

}
