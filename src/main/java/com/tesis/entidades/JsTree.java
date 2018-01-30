package com.tesis.entidades;

import java.util.ArrayList;

public class JsTree {
	private int id;
	private String text;
	private String parent;
	private ArrayList<JsTree> children;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	public String getParent() {
		return parent;
	}
	public void setParent(String parent) {
		this.parent = parent;
	}
	public ArrayList<JsTree> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<JsTree> children) {
		this.children = children;
	}

}
