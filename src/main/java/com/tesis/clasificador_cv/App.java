package com.tesis.clasificador_cv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.encryption.InvalidPasswordException;
import org.apache.pdfbox.text.PDFTextStripper;

import com.github.andrewoma.dexx.collection.List;
import com.tesis.entidades.IndividuoTI;
import com.tesis.entidades.PorcSim;
import com.tesis.utilitarios.Util;

import info.debatty.java.stringsimilarity.JaroWinkler;

public class App 
{
    public static void main( String[] args )
    {
    	/*
        //Loading an existing document
        File file = new File("/Users/JONATHAN/Desktop/JONATHAN_IGLESIAS_MORALES.pdf");
        PDDocument document = PDDocument.load(file);

        //Instantiate PDFTextStripper class
        PDFTextStripper pdfStripper = new PDFTextStripper();

        //Retrieving text from PDF document
        String salto = System.getProperty("line.separator");
        
        String text = pdfStripper.getText(document);
        String[] lines = text.split(salto);       
        
        //System.out.println(document.getPages().getCount());        

        for(String line : lines){
        	if(line.trim().length() > 0) {
        		
        		String linePrepocesada = Util.stripAccents(line.trim().toLowerCase());        		
        		//System.out.println("**" + linePrepocesada);
        		//System.out.println("***" + );        		
        		//System.out.println("ASCII value of " + text + " is following");
    			System.out.println( Util.stringToASCII(linePrepocesada) );
        	}
        }
        //System.out.println(text);

        //Closing the document
        document.close();*/
        

		String fileNameOnto = "/Users/JONATHAN/Desktop/ONTOLOGIA/TI.rdf";
		
		try {
			File file_onto = new File(fileNameOnto);
			FileReader reader = new FileReader(file_onto);
			OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
			
			model.read(reader,null);
			
			ArrayList<IndividuoTI> individuos = new ArrayList<IndividuoTI>();
			
			Iterator individuals = model.listIndividuals();
			while(individuals.hasNext()){
				Individual individual = (Individual) individuals.next();
				
				IndividuoTI IndTmp = new IndividuoTI();
				
				IndTmp.setIndividuo(individual);
				
				String descripcion_tmp = individual.getLocalName().replace("_"," ").toLowerCase();
				
				IndTmp.setDescripcion(descripcion_tmp);
				IndTmp.setLongitud(descripcion_tmp.length());
				
				individuos.add(IndTmp);
			}			
			
			/*for(IndividuoTI item : individuos){
				System.out.println(item.getDescripcion());
			}*/
			
			// Empezar a leer archivos PDFs
			
			System.out.println("Empezar a leer archivos PDFs");
			System.out.println("----------------------------");
			
			try {
				
		        //Loading an existing document
		        File file = new File("/Users/JONATHAN/Desktop/JONATHAN_IGLESIAS_MORALES.pdf");
		        PDDocument document = PDDocument.load(file);
	
		        //Instantiate PDFTextStripper class
		        PDFTextStripper pdfStripper = new PDFTextStripper();
	
		        //Retrieving text from PDF document
		        String salto = System.getProperty("line.separator");		        
		        String text = pdfStripper.getText(document);
		        //Closing the document
		        document.close();
		        
		        String[] lines = text.split(salto);		      
	
		        ArrayList<PorcSim> arrIndividuoEncont = new ArrayList<PorcSim>();
		        
		        int numLine = 0;
		        for(String line : lines){
		        	if(line.trim().length() > 0) {
		        		numLine++;
		        		String linePrepocesada = Util.stripAccents(line.trim().toLowerCase());
		        		String lineClean = Util.stringToASCII(linePrepocesada);		        		
		        		
		        		searchOntoLoop:
		        		for(IndividuoTI item : individuos){
		        			// Recorriendo todos los individuos de la Ontología	        		
		        			
		        			if( lineClean.length() >= item.getLongitud() ) {
								
								int caract_delimitador = lineClean.length() - item.getLongitud();
								
								for(int x = 0; x <= caract_delimitador ; x++){
									
							        JaroWinkler jw = new JaroWinkler();
									String subcadena = lineClean.substring(x, x + item.getLongitud());									
									double porcSim = jw.similarity(item.getDescripcion(), subcadena);
									
									if( porcSim >= 0.8 ){
						        		/*System.out.println("Evaluando: " + lineClean);
						        		System.out.println("Tam: " + lineClean.length() );										
										
										System.out.println("Nodo Onto: " + item.getDescripcion());										
										System.out.println("Substring = " + subcadena + " Porc.: " + porcSim);
						    			System.out.println("----------------------------");*/					    			
						    			
						    			PorcSim sim = new PorcSim();
						    			sim.setIndividuo(item);
						    			sim.setNumLinea(numLine);
						    			sim.setDescLinea(lineClean);
						    			sim.setSubCadena(subcadena);
						    			sim.setPorcentaje(porcSim);
						    			
						    			arrIndividuoEncont.add(sim);
						    			break searchOntoLoop;
									}								
								}		        			
		        			}else continue;
						}		        	
		        	}
		        }
	
			
		        for(PorcSim encontrado : arrIndividuoEncont) {
		        	System.out.println("Individuo: " + encontrado.getIndividuo().getDescripcion());
		        	System.out.println("Subcadena: " + encontrado.getSubCadena());
		        	System.out.println("Linea: " + encontrado.getNumLinea());
		        	System.out.println("Porcentaje: " + encontrado.getPorcentaje());
		        }
		        
		        
			} catch (Exception e){
				System.out.println(e.getMessage());
				e.printStackTrace();				
			}
			
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Archivo no encontrado");
			e.printStackTrace();
		}
        
        
    }
}
