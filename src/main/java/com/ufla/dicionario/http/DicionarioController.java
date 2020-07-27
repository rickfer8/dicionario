package com.ufla.dicionario.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ufla.dicionario.json.DicionarioJson;
import com.ufla.dicionario.service.DicionarioService;

@RestController
@RequestMapping(value = "dicionario")
public class DicionarioController {
	
	@Autowired
	private DicionarioService dicionarioService;
	
	@RequestMapping(path = "/{palavra}", method = RequestMethod.GET)
	public ResponseEntity<DicionarioJson> dicionario(@PathVariable("palavra") String palavra) {
		DicionarioJson retorno = dicionarioService.getPosicao(palavra);		
		return new ResponseEntity<DicionarioJson>(retorno, HttpStatus.OK);
	}

}
