package com.tesis.clasificador_cv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.ontology.OntProperty;
import org.apache.jena.ontology.Ontology;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;

import com.tesis.entidades.IndividuoTI;

public class UpdateOnto {

	public static void main(String[] args) throws FileNotFoundException {
		// TODO Auto-generated method stub
		
		// Lectura de ontologia a modificar
		
		String fileNameOntoInput = "/Users/JONATHAN/Desktop/ONTOLOGIA/TI.rdf";
		
		File file_onto = new File(fileNameOntoInput);
		FileReader reader = new FileReader(file_onto);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		
		//OntModelSpec.OWL_DL_MEM
		
		model.read(reader,null);
	    model.write(System.out,"RDF/XML");
			
		
		// crear una ontología teniendo como base al input
		//OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF, model);
		
		String ontologyURI = null;
		String ontologyNS = null;
		Iterator iter = model.listOntologies();
		    
		if(iter.hasNext()){
		    Ontology onto = (Ontology) iter.next();		    
		    ontologyURI = onto.getURI();
		    ontologyNS = onto.getNameSpace();
		    System.out.println("Ontology URI = "+ontologyURI);
		    System.out.println("Ontology NS = "+ontologyNS);
		}
		
		// Obtener individuos
		
		Iterator individuals = model.listIndividuals();
		while(individuals.hasNext()){
			Individual individual = (Individual) individuals.next();
			
			System.out.println("----------------------------");
			System.out.println(individual.getLocalName());
			System.out.println("--> Propiedades");
			
			//individual.getc
			
			StmtIterator propIter = individual.listProperties();

	        while (propIter.hasNext()) {
	        	Statement s = (Statement) propIter.next();

	            if (s.getObject().isLiteral()) {
	                //System.out.println(""+s.getLiteral().getLexicalForm().toString()+" type = "+s.getPredicate().getLocalName());
	            	System.out.println(s.getPredicate().getLocalName() + " - " + s.getLiteral().getLexicalForm().toString());
	            }
	        }
	        
			System.out.println("----------------------------");
		}
		
		
		//create classes
		OntClass person = model.createClass(ontologyURI+"#Person");
		OntClass student = model.createClass(ontologyURI+"#Student");
		OntClass professor = model.createClass(ontologyURI+"#Professor");
		
		//set sub-classes
		person.addSubClass(student);
		person.addSubClass(professor);
		
		// Crear individuo        
        Individual paper = model.createIndividual(ontologyURI + "#paper1", student);
		
		// Grabar en archivo la ontología modificada.
		
		StringWriter sw = new StringWriter();
		model.write(sw, "RDF/XML");
		String owlCode = sw.toString();
		
		File file = new File("/Users/JONATHAN/Desktop/ONTOLOGIA/TI_MOD.rdf");
		try{
		    FileWriter fw = new FileWriter(file);
		    fw.write(owlCode);
		    fw.close();
		} catch(FileNotFoundException fnfe){
		    fnfe.printStackTrace();
		} catch(IOException ioe){
		    ioe.printStackTrace();
		}
	}

}
