package com.scut.server.parser.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.scut.server.serlvet.ChannelInf;
import com.scut.server.serlvet.HttpRequest;
import com.scut.server.serlvet.HttpResponse;

public class HttpParser {
	
	//包含method, uri, http版本
	private Map<String, String> header = null;
	
	private boolean isParsed = false;
	
	public HttpParser() {
		header = new HashMap<String, String>();
	}
	
	public HttpRequest getHttpRequest(ChannelInf channelInf) {
		if (!isParsed()) {
			return null;
		}

		return new HttpRequest(channelInf, getValue("Body"));
	}
	
	public HttpResponse getHttpResponse(ChannelInf channelInf) {
		if (!isParsed()) {
			return null;
		}
		
		return new HttpResponse(channelInf);
	}
	
	public String getValue(String key) {
		return header.get(key);
	}
	
	public void parse(BufferedReader reader) {
		String line = null;
		
		try {
			line = reader.readLine();
			//System.out.println(line);
			String[] firsts = line.split(" ");
			header.put("Method", firsts[0]);
			//System.out.println(firsts[0]);
			header.put("Uri", firsts[1]);
			//System.out.println(firsts[1]);
			header.put("Version", firsts[2]);
			//System.out.println(firsts[2]);
			
			while ((line = reader.readLine()) != null) {
				if (line.equals("")) {
                    break;
                }
				//System.out.println(line);
				String[] lineElements = line.split(" ");
				if (lineElements[0].equals("Host:") ||
					lineElements[0].equals("Connection:")) {
					header.put(lineElements[0].substring(0, 
							lineElements[0].lastIndexOf(':')), 
							lineElements[1]);
				}
			}
			
			if (header.get("Method").equals("POST")) {
				header.put("Body", 
						java.net.URLDecoder.decode(        //解码post参数
								reader.readLine(),"UTF-8"));
			}
			
			isParsed = true;
			
			//System.out.println(header);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void clear() {
		isParsed = false;
		header.clear();
	}
	
	public boolean isParsed() {
		return isParsed;
	}
	
	
	
	public static void main(String[] args) {
		try {
			ServerSocket server = new ServerSocket(9988);
				Socket client = null;
				client = server.accept();
				
				BufferedReader reader = new BufferedReader(
                        new InputStreamReader(client.getInputStream()));
				HttpParser p = new HttpParser();
				p.parse(reader);
				System.out.println(p.header);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
