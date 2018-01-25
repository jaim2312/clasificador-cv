package com.tesis.utilitarios;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;

public class Util {
	
	public static String /*ArrayList<Integer>*/ stringToASCII(String text) {
		
		// converting String to ASCII value in Java		
		try { 
			
			String clearCad = "";
			
			int[] byteNotAuth = new int[]{63};
			
			// String text = "ABCDEFGHIJKLMNOP";
			// translating text String to 7 bit ASCII encoding
			byte[] bytes = text.getBytes("US-ASCII");
			// System.out.println("ASCII value of " + text + " is following");
			// System.out.println(Arrays.toString(bytes));
						
			ArrayList<Byte> bytesToArray = asciiBytesToArray(bytes);
			
			ArrayList<Integer> positions = new ArrayList<Integer>();
			
			for(int i = 0 ; i < byteNotAuth.length; i++) {
									
				//System.out.println("Encontrado " + byteNotAuth[i]);
				
				// Identificar en que posiciones se encuentra el caracter no autorizado
				for(int j = 0; j < bytes.length; j++){						
					if( bytes[j] == byteNotAuth[i] ){
						positions.add(j);
					}
				}
					
			}
									
			// Crear cadena sin caracteres no permitidos
			for (int x = 0; x < text.length(); x++) {
				
				if( !positions.contains(x) ){
					clearCad = clearCad + text.charAt(x);
				}
			}
				   
			// return positions;
			return clearCad.trim();
			
		} catch (java.io.UnsupportedEncodingException e) { 
			e.printStackTrace();
		}
		
		return null;
	}
	
	public static String removeCharAt(String s, int pos) {
		return s.substring(0, pos) + s.substring(pos + 1);
	}
	
	public static ArrayList<Byte> asciiBytesToArray( byte[] bytes ) {
		
		if ( (bytes == null) || (bytes.length == 0) ) return new ArrayList<Byte>();
	  	  	  
		ArrayList<Byte> listado = new ArrayList<Byte>();  
		
		for ( int i = 0; i < bytes.length; i++ ) {
			listado.add(new Byte(bytes[i]));
		}
	  
		return listado;
	}
	
	public static String stripAccents(String s) 
	{
	    s = Normalizer.normalize(s, Normalizer.Form.NFD);
	    s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
	    return s;
	}	
}
