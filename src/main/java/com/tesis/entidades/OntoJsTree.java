package com.tesis.entidades;

import org.apache.jena.ontology.OntResource;

public class OntoJsTree {
	private OntResource nodeChild;
	private OntResource nodeParent;
	
	public OntoJsTree(OntResource nodeChild, OntResource nodeParent) {
		super();
		this.nodeChild = nodeChild;
		this.nodeParent = nodeParent;
	}

	public OntResource getNodeChild() {
		return nodeChild;
	}

	public void setNodeChild(OntResource nodeChild) {
		this.nodeChild = nodeChild;
	}

	public OntResource getNodeParent() {
		return nodeParent;
	}

	public void setNodeParent(OntResource nodeParent) {
		this.nodeParent = nodeParent;
	}
}
