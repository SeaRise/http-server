package com.scut.test;

import com.scut.client.ClientChannel;

public class ClientTest {

	public static void main(String[] args) {
		ClientChannel clientChannel = new ClientChannel();
		clientChannel.connect("127.0.0.1", 9988);
		String mess = new String();
		clientChannel.send(mess);
		clientChannel.close();
	}

}
