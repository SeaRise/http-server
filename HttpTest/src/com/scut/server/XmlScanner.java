package com.scut.server;

import java.util.HashMap;
import java.util.Map;

import com.scut.server.parser.xml.DocumentTree;
import com.scut.server.parser.xml.XmlNode;
import com.scut.server.serlvet.ChannerHandler;

public class XmlScanner {
	
	private Map<String, String> servletMap; 
	
	private Map<String, ChannerHandler> serlvetCaseMap;
	
	private String xmlPath;
	
	private DocumentTree dt = null;
	
	public XmlScanner(String xmlPath) {
		servletMap = new HashMap<String, String>(); 
		serlvetCaseMap = new HashMap<String, ChannerHandler>(); 
		this.xmlPath = xmlPath;
		dt = new DocumentTree();
	}
	
	public ChannerHandler getServlet (String servletName) {
		ChannerHandler servlet = null;
		if ((servlet = serlvetCaseMap.get(servletName)) != null) {
			return servlet;
		}
		
		Class c;
		try {
			c = Class.forName(servletMap.get(servletName));
			servlet = (ChannerHandler) c.newInstance();
			serlvetCaseMap.put(servletName, servlet);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}  
		return servlet;
	}
	
	public void scan() {
		dt.parse(xmlPath);
		if (!dt.isParsed()) {
			System.out.println("scan error");
			System.exit(-1);
		}
		String servletName = null;
		String servletClass = null;
		XmlNode root = dt.getRootNode();
		for (int i = 0; i < root.getLength(); i++) {
			XmlNode child = root.get(i);
			for (int j = 0; j < child.getLength(); j++) {
				if (child.get(j).getKey().equals("servlet-name")) {
					servletName = child.get(j).getValue();
				}
				if (child.get(j).getKey().equals("servlet-class")) {
					servletClass = child.get(j).getValue();
				}
			}
			servletMap.put(servletName, servletClass);
		}
	}
}
