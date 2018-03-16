package com.tesis.clasificador_cv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
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

import com.google.gson.Gson;
import com.tesis.entidades.IndividuoTI;
import com.tesis.entidades.JsTree;
import com.tesis.entidades.OntoJsTree;

public class UpdateOnto {

	public static void main(String[] args) throws FileNotFoundException {
		
		// Lectura de ontologia a modificar
		ClassLoader classLoader = UpdateOnto.class.getClassLoader();
		File file_onto = new File(classLoader.getResource("TI.rdf").getFile());		
		//Este arreglo guardará los nodos de la ontología
		ArrayList<OntoJsTree> listOntoNodo = new ArrayList<OntoJsTree>();
		
		FileReader reader = new FileReader(file_onto);
		OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM);
		
		model.read(reader,null);
	    //model.write(System.out,"RDF/XML");
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
		
		// Recorremos la ontologia y guardamos cada nodo en una lista
		for (Iterator<OntClass> i = model.listClasses();i.hasNext();){
			OntClass cls = i.next();			
			OntClass clsParent = cls.getSuperClass();
			
			listOntoNodo.add(new OntoJsTree(cls, clsParent));
			
			for(Iterator it = cls.listInstances(true);it.hasNext();){
				Individual ind = (Individual) it.next();
				if(ind.isIndividual()){
					listOntoNodo.add(new OntoJsTree(ind, cls));
				}
			}
		}
		
		
		for(OntoJsTree item : listOntoNodo){
			
			if(item.getNodeParent() != null)			
				System.out.println("Child: " + item.getNodeChild().getLocalName() + " - Parent: " + item.getNodeParent().getLocalName());
			else
				System.out.println("Child: " + item.getNodeChild().getLocalName() + " - Parent: no tiene" );
		}
		
		
		ArrayList<JsTree> nodesList = new ArrayList<JsTree>();

        for (OntoJsTree ontoNodo : listOntoNodo){
        	
            if (ontoNodo.getNodeParent() == null){

            	JsTree jsNodo = new JsTree();
            	//jsNodo.setId( ontoNodo.getNodeChild() );
            	jsNodo.setParent("#");
            	jsNodo.setText( ontoNodo.getNodeChild().getLocalName() );
                //$node->a_attr = new StdClass();                
                
                if (GrupoTieneHijos(ontoNodo.getNodeChild().getLocalName() , listOntoNodo) == true){
                    
                        //$node->state = new StdClass();
                        //$node->state->opened = TRUE;


                	ArrayList<JsTree> resultado = ConstruirNodoHijo(listOntoNodo, ontoNodo);

                    if (resultado != null) jsNodo.setChildren(resultado);
                }
                nodesList.add(jsNodo);
            }
        }
        
        
        Gson gson = new Gson();
        String json = gson.toJson(nodesList);
        
        System.out.println(json);
		
		
		/*
		//create classes
		OntClass person = model.createClass(ontologyURI+"#Person");
		OntClass student = model.createClass(ontologyURI+"#Student");
		OntClass professor = model.createClass(ontologyURI+"#Professor");
		
		//set sub-classes
		person.addSubClass(student);
		person.addSubClass(professor);
		
		// Crear individuo
        Individual paper = model.createIndividual(ontologyURI + "#paper1", student);*/
		
		// Grabar en archivo la ontología modificada.
		
		/*StringWriter sw = new StringWriter();
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
		}*/
	}
	
    protected static boolean GrupoTieneHijos(String idpadre, ArrayList<OntoJsTree> listado)
    {

        boolean tieneHijo = false;
        for (OntoJsTree node : listado) {
            if(node.getNodeParent() != null ){
	            if ( node.getNodeParent().getLocalName().equals(idpadre) ) {
	                tieneHijo = true;
	                break;
	            }
            }
        }
        return tieneHijo;
    }

	protected static ArrayList<JsTree> ConstruirNodoHijo(ArrayList<OntoJsTree> listado, OntoJsTree nodo)
	{
	   	ArrayList<JsTree> listadoHijos = new ArrayList<JsTree>();

	    if (nodo.getNodeChild() != null) {
	        int hijos = 0;

	        for(OntoJsTree nodo_recorrido_actual : listado) {
	        	if(nodo_recorrido_actual.getNodeParent() != null ){
		            if ( nodo_recorrido_actual.getNodeParent().getLocalName().equals(nodo.getNodeChild().getLocalName()) ) {
		                JsTree nodoTmp = new JsTree();
	                	//$node->id = $contexto_recorrido_actual['i;
		                nodoTmp.setText( nodo_recorrido_actual.getNodeChild().getLocalName() );
	                    
	                	//$node->a_attr = new StdClass();
	                    //$node->a_attr->data_es_modal = $contexto_recorrido_actual['esmodal'];
	
		                if (GrupoTieneHijos(nodo_recorrido_actual.getNodeChild().getLocalName(), listado))
		                {
	                        //$node->state = new StdClass();
	                        //$node->state->opened = TRUE;
	
	                        ArrayList<JsTree> resultado = ConstruirNodoHijo(listado, nodo_recorrido_actual);
	                        nodoTmp.setChildren(resultado);
		                }
	
		                listadoHijos.add(nodoTmp);
	
		            }
	        	}
	        }
	    }

	    return listadoHijos;
	}

}
