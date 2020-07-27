package com.ufla.dicionario.util;

import java.text.Normalizer;

public class DicionairoUtil {
	
	public static String validarPalabra(String palavra) {
		palavra = palavra.replace("\"", "");
	    return Normalizer.normalize(palavra, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}

}
