package com.tesis.clasificador_cv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.jena.ontology.Individual;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
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
		String fileNameOnto = "/Users/JONATHAN/Desktop/ONTOLOGIA/TI.rdf";
		
		try {
			File file_onto = new File(fileNameOnto);
			FileReader reader = new FileReader(file_onto);
			OntModel model = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_RDFS_INF);
			
			model.read(reader,null);			
			
			// Empezar a leer archivos PDFs
			ArrayList<Path> fileProcess = new ArrayList<Path>();
			
			try {
				Files.walk(Paths.get("/Users/JONATHAN/Desktop/CV")).forEach(ruta-> {
				    if (Files.isRegularFile(ruta)) {
				    	//Filtrar archivo por extensión				    	
				    	Pattern p = Pattern.compile("^.*(pdf|PDF)$");
				        Matcher m = p.matcher(ruta.toString());
				        
				        if(m.matches()) fileProcess.add(ruta);
				    }
				});
			} catch (IOException ex) {
				System.out.println("Error leyendo cvs:"+ex.getMessage());
			}
			
			
			System.out.println("Empezar a leer archivos PDFs");
			System.out.println("----------------------------");
			
			for(Path cvActual : fileProcess){
			
				try {
					
			        //Loading an existing document
					File file = cvActual.toFile();
					System.out.println(cvActual);
					System.out.println();
					
			        PDDocument document = PDDocument.load(file);
		
			        //Instantiate PDFTextStripper class
			        PDFTextStripper pdfStripper = new PDFTextStripper();
		
			        //Retrieving text from PDF document
			        String salto = System.getProperty("line.separator");		        
			        String text = pdfStripper.getText(document);
			        //Closing the document
			        document.close();
			        
			        String[] lines = text.split(salto);
			        
			        //Listar de individuos a evaluar
			        ArrayList<IndividuoTI> individuos = Util.getIndividuoTIByModel(model);			        
		
			        ArrayList<PorcSim> arrIndividuoEncont = new ArrayList<PorcSim>();
			        
			        int numLine = 0;
			        for(String line : lines){
			        	if(line.trim().length() > 0) {
			        		numLine++;
			        		String linePrepocesada = Util.stripAccents(line.trim().toLowerCase());
			        		String lineClean = Util.moreSpaceToOne( Util.stringToASCII(linePrepocesada) );
			        		
			        		
			        		for(IndividuoTI item : individuos){
			        			if(item.isbEncontrado() != true){
			        				
			        				//System.out.println(item.getDescripcion() + " - " + item.isbEncontrado());
			        			
				        			// Recorriendo todos los individuos de la Ontología
				        			java.util.List<String> ind_sinonimos = new ArrayList<String>();
				        			
				        			//System.out.println(item.getDescripcion());
				        			
				        			// Obteniendo los sinonimos del individuo
				        			StmtIterator props = item.getIndividuo().listProperties();
				        			while (props.hasNext()) {
				        				Statement s = props.next();
		
				                        if (s.getObject().isLiteral() && s.getPredicate().getLocalName().equals("sinonimo") ) {
				                        	String[] sinonimos = s.getLiteral().getLexicalForm().split(",");
				                        	ind_sinonimos.addAll(Arrays.asList(sinonimos));
				                        	break;
				                        }   				
				        			}
				        			
				        			ind_sinonimos.add(item.getDescripcion());
				        			
				        			//System.out.println(numLine+ " - " + lineClean +" - "+ind_sinonimos);
				        			System.out.println(numLine + " - " + ind_sinonimos);
				        			
				        			searchOntoLoop:
				        			// Recorro al individuo con todo sus sinonimos
				        			for(String ind_sin : ind_sinonimos){
				        				// Verifico cuantas palabras tiene el sinónimos
				        				String[] sinoni_palab = Util.arrWords(ind_sin);

				        				//System.out.println(sinoni_palab);
				        				
				        				if(sinoni_palab.length > 1){
				        					//System.out.println("Tiene mas de una palabra: "+ind_sin);
				        					
				        					String[] line_palab = Util.arrWords(lineClean);
				        					
				        					for(int i=0;i<line_palab.length-sinoni_palab.length+1;i++){
				        						
				        						String subcadena = Util.getNumWordByN(line_palab,i+1,sinoni_palab.length);
				        						
				        						//System.out.println("\t"+Util.getNumWordByN(line_palab,i+1,sinoni_palab.length));
				        						
				        						JaroWinkler jw = new JaroWinkler();									
												double porcSim = jw.similarity(ind_sin, subcadena);												
												
												if( porcSim >= 0.9 ){
						    										    			
									    			PorcSim sim = new PorcSim();
									    			sim.setIndividuo(item);
									    			sim.setNumLinea(numLine);
									    			sim.setDescLinea(lineClean);
									    			sim.setSubCadena(subcadena);
									    			sim.setPorcentaje(porcSim);
									    			
									    			//Individuo encontrado
									    			item.setbEncontrado(true);
									    			
									    			arrIndividuoEncont.add(sim);
									    			break searchOntoLoop;
												}
				        						
				        					}				        					
				        					
				        				}else{
				        					// Obtener todas las palabras de la cadena y compararlas con el sinonimo
				        					String[] line_palab = Util.arrWords(lineClean);		        					
				        					
				        					for(String palabra : line_palab){				        						
				        						
				        						JaroWinkler jw = new JaroWinkler();									
												double porcSim = jw.similarity(ind_sin, palabra);												
												
												if( porcSim >= 0.9 ){
						    										    			
									    			PorcSim sim = new PorcSim();
									    			sim.setIndividuo(item);
									    			sim.setNumLinea(numLine);
									    			sim.setDescLinea(lineClean);
									    			sim.setSubCadena(palabra);
									    			sim.setPorcentaje(porcSim);
									    			
									    			//Individuo encontrado
									    			item.setbEncontrado(true);
									    			
									    			//System.out.print(" Encontrado\n");
									    			
									    			arrIndividuoEncont.add(sim);
									    			break searchOntoLoop;
												}			        						
				        					}
				        				}				        								        				
					        			
				        			}
								}
			        		}
			        	}
			        }
		
			        //System.out.println("---------------------------------------");
				
			        for(PorcSim encontrado : arrIndividuoEncont) {
			        	System.out.println("Individuo: " + encontrado.getIndividuo().getDescripcion());
			        	System.out.println("\tSubcadena: " + encontrado.getSubCadena());
			        	System.out.println("\tNº Linea: " + encontrado.getNumLinea());
			        	System.out.println("\tLinea: " + encontrado.getDescLinea() );
			        	System.out.println("\tPorcentaje: " + encontrado.getPorcentaje());
			        }
			        
			        
				} catch (Exception e){
					System.out.println(e.getMessage());
					e.printStackTrace();				
				}
				
				System.out.println("---------------------------------------");
			
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Archivo RDF no encontrado");
			e.printStackTrace();
		}
        
        
    }
}
