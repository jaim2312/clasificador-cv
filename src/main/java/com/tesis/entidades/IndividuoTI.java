package com.tesis.entidades;

import org.apache.jena.ontology.Individual;

public class IndividuoTI {
	private Individual individuo;
	private String descripcion;
	private int longitud;
	
	public Individual getIndividuo() {
		return individuo;
	}
	public void setIndividuo(Individual individuo) {
		this.individuo = individuo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getLongitud() {
		return longitud;
	}

	public void setLongitud(int longitud) {
		this.longitud = longitud;
	}
}
