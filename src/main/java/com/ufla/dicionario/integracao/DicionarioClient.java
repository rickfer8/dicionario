package com.ufla.dicionario.integracao;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ufla.dicionario.exceptions.DicionarioException;
import com.ufla.dicionario.exceptions.DicionarioResponseErroHandler;
import com.ufla.dicionario.json.UflaJson;
import com.ufla.dicionario.util.DicionairoUtil;

@Service
public class DicionarioClient {
	
	@Value("${ufla.link}")
	private String link;
	
	public UflaJson getDicionario(Integer posicao)  {
		String palavra = "";
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DicionarioResponseErroHandler());
		UflaJson json = new UflaJson();
		final String uflaUrl = link + "/" + posicao;
		URI uri;
		try {
			uri = new URI(uflaUrl);
			ResponseEntity<String> response = restTemplate.getForEntity(uri, String.class);
			
			palavra = response.getBody();
			palavra = DicionairoUtil.validarPalabra(palavra);			
			json.setPalavra(palavra);
			json.setStatus(response.getStatusCode().name());
			
		} catch (URISyntaxException e) {
			throw new DicionarioException(e.getMessage());			
		}
		
		return json;
	}

}
