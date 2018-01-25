package com.tesis.clasificador_cv;

import java.io.File;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class UpdateOntoOWL {

	public static void main(String[] args) throws OWLOntologyCreationException {
		// TODO Auto-generated method stub

		String fileNameOnto = "/Volumes/DATOS/TI_OWL.owl";
		
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		File file = new File(fileNameOnto);
		OWLOntology o = man.loadOntologyFromOntologyDocument(file);
		System.out.println(o);
		
		
		
		
		
	}

}
