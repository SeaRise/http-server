package com.scut.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Test implements Runnable {

	private final static int PORT = 9988;
    private ServerSocket server = null;
    
	public static void main(String[] args) {
		new Test(); 
	}
	
	public Test() {
        try {
            server = new ServerSocket(PORT);
            if (server == null)
                System.exit(1);
            new Thread(this).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	@Override
	public void run() {
		while (true) { 
             try {
            	Socket client = null;
				client = server.accept();
				
				BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
				StringBuffer mess = new StringBuffer();
				String line = null;
				
				while ((line = reader.readLine()) != null) {
					
                    if (line.equals("")) {
                        break;
                    }
                    
                    //System.out.println(line);
					mess.append(line + "\n");
                }
				
				System.out.println(mess.toString());
				
				String str = "<html><body><h1>fds打撒</h1><p>fdfdfd</p></body></html>";
				PrintStream writer = new PrintStream(client.getOutputStream());
                writer.println("HTTP/1.1 200 OK");// 返回应答消息,并结束应答
                writer.println("Content-Type:text/html; charset=UTF-8");
                writer.println("Content-Length:" + str.length());// 返回内容字节数
                writer.println();// 根据 HTTP 协议, 空行将结束头信息
				writer.println(str);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
