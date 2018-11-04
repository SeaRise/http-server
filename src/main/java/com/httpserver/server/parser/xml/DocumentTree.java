package com.httpserver.server.parser.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class DocumentTree {
	
	private XmlNode rootNode = null;
	
	private boolean isParsed = false;
	
	public DocumentTree() {
		rootNode = new XmlNode();
	}
	
	public void parse(String filePath) {
		if (filePath == null || !filePath.endsWith(".xml")) {
			return;
		}

		FileInputStream fis = null;		
		try {
			fis = new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		}
		
		StringBuffer key = new StringBuffer();     
        char c;

		try {
			while ((c = (char) fis.read()) != '<') {
				if (c == -1) {
					fis.close();
					return;
				}
			}
			
			while ((c = (char) fis.read()) != '>') {
				if (c == -1) {
					fis.close();
					return;
				}
				key.append(c);
			}
			
			if (!parseNode(fis, rootNode, key.toString())) {
				return;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		isParsed = true;
	}
	
	boolean parseNode(FileInputStream fis, XmlNode node, String startKey) throws IOException {
		
        StringBuffer endKey = new StringBuffer();
        StringBuffer value = new StringBuffer();
        
        char c;
        
        String label;
        
        boolean hasAttr = false;
		
		node.setKey(startKey);
		
        while (true) {
        	while ((c = (char) fis.read()) != '<') {
    			if (c == -1) {
    				return false;
    			}
    			value.append(c);
    		}
    		
    		while ((c = (char) fis.read()) != '>') {
    			if (c == -1) {
    				return false;
    			}
    			endKey.append(c);
    		}
    		//
    		String[] strs = endKey.toString().split(" ");
    		String[] keyAndAttr = null;
    		if (strs.length > 2) {
    			return false;
    		}
    		if (strs.length == 2) {
    			keyAndAttr = strs[1].split("=");
    			if (keyAndAttr.length != 2) {
    				return false;
    			}
    			hasAttr = true;
    		}
    		label = strs[0];
    		//System.out.println(label);
    		//
    		if (label.equals("/" + node.getKey())) {
    			if (node.isLeaf()) {
    				node.setValue(value.toString().trim());
    			}
    			break;
    		} else {
    			XmlNode childNode = new XmlNode();
    			node.addChild(childNode);
    			
    			if (hasAttr) {
    				XmlNode grandsonNode = new XmlNode();
    				childNode.addChild(grandsonNode);
        			grandsonNode.setKey(keyAndAttr[0].trim());
        			String s = keyAndAttr[1].trim();
        			grandsonNode.setValue(s.substring(1, (s.length() - 2)));
    				hasAttr = false;
    			}
    			
    			if (!parseNode(fis, childNode, label)) {
    				return false;
    			}
    			
    			endKey.delete(0, endKey.length());
    			value.delete(0, value.length());
    		}
        }
        
		return true;
	}
	
	public boolean isParsed() {
		return isParsed;
	}
	
	public XmlNode getRootNode() {
		return rootNode;
	}
	
}
