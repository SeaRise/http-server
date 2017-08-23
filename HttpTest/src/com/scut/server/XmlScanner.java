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
	
	private ChannelContext channelContext = null;
	
	public XmlScanner(String xmlPath, ChannelContext channelContext) {
		servletMap = new HashMap<String, String>(); 
		serlvetCaseMap = new HashMap<String, ChannerHandler>(); 
		this.xmlPath = xmlPath;
		dt = new DocumentTree();
		this.channelContext = channelContext;
		scan();
		//System.out.println(servletMap);
	}
	
	public void close() {
		//遍历serlvetCaseMap,销毁servlet实例
		for (ChannerHandler v : serlvetCaseMap.values()) {
			   v.destroy(channelContext);
	    }
		servletMap.clear();
		servletMap = null;	
		serlvetCaseMap.clear();
		serlvetCaseMap = null;
	}
	
	public ChannerHandler getServlet (String servletName) {
		ChannerHandler servlet = null;
		if ((servlet = serlvetCaseMap.get(servletName)) != null) {
			return servlet;
		}
		
		Class c;
		try {
			String servletClass = servletMap.get(servletName);
			
			if (servletClass == null) {
				return null;
			}
			
			c = Class.forName(servletClass);
			servlet = (ChannerHandler) c.newInstance();
			servlet.init(channelContext);
			
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
		//System.out.println("scan");
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
