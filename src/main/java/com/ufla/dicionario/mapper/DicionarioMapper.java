package com.ufla.dicionario.mapper;

import com.ufla.dicionario.entidade.Dicionario;
import com.ufla.dicionario.json.DicionarioJson;

public class DicionarioMapper {
	
	public static DicionarioJson mapper(Dicionario dicionario, String status, Integer gatinho) {
		
		DicionarioJson json = new DicionarioJson();
		json.setGatinho(gatinho);
		json.setMensagem(status);
		json.setPosicao(dicionario.getPosicao());
		
		return json;
		
	}

}
