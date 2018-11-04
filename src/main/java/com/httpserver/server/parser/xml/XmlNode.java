package com.httpserver.server.parser.xml;

import java.util.ArrayList;
import java.util.List;

public class XmlNode {
	
	private String key = null;
	
	private String value = null;
	
	private boolean isLeaf = true;
	
	List<XmlNode> childrenList = null;
			
	
	public String getKey() {
		return key;
	}
	
	void setKey(String key) {
		this.key = key;
	}
	
	void setValue(String value) {
		this.value = value;
	}
	
	void addChild(XmlNode childNode) {
		if (childrenList == null) {
			childrenList = new ArrayList<XmlNode>();
			isLeaf = false;
		}
		
		childrenList.add(childNode);
	}
	
	public XmlNode findChildByKey(String key) {
		for (int i = 0; i < childrenList.size(); i++) {
			if (childrenList.get(i).equals(key)) {
				return childrenList.get(i);
			}
		}
		
		return null;
	}
	
	public int getLength() {
		return childrenList.size();
	}
	
	public XmlNode get(int i) {
		return childrenList.get(i);
	}
	
	public String getValue() {
		return value;
	}
	
	public boolean isLeaf() {
		return isLeaf;
	}
	
}
