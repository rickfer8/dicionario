package com.ufla.dicionario.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ufla.dicionario.dao.DicionarioDAO;
import com.ufla.dicionario.entidade.Dicionario;
import com.ufla.dicionario.integracao.DicionarioClient;
import com.ufla.dicionario.json.DicionarioJson;
import com.ufla.dicionario.json.UflaJson;
import com.ufla.dicionario.mapper.DicionarioMapper;
import com.ufla.dicionario.util.DicionairoUtil;

@Service
public class DicionarioService {
	
	@Autowired
	private DicionarioDAO dao;
	
	@Autowired
	private DicionarioClient dicionarioClient;
	
	/**
	 * Método responsável por recuperar a posição da palavra no dicionário
	 * 
	 * @author ricardo.ribeiro
	 * 
	 * @param palavra - Palavra Pesquisada
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */

	public DicionarioJson getPosicao(String palavra) {
		palavra = DicionairoUtil.validarPalabra(palavra);
		return pecorrerDicionario(palavra);
	}
	

	/**
	 * Método responsável por pecorrer o dicionário para encontrar a palavra
	 * 
	 * @author ricardo.ribeiro
	 * 
	 * @param palavra - Palavra Pesquisada
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	public DicionarioJson pecorrerDicionario(String palavra) {		
		String chave = getChave(palavra);
		List<Dicionario> dicionarios = dao.findAllChave(chave);
		Dicionario dicionario = recuperarDicionario(dicionarios, palavra);
		
		if(dicionario.getDescricao() != null) {
			UflaJson ufla = consumirServicoDicionario(dicionario.getPosicao());
			return DicionarioMapper.mapper(dicionario, ufla.getStatus(), BigDecimal.ONE.intValue());			
		}else {			
			return recuperarPalavra(palavra);
		}		
	}
	
	/**
	 * Método responsável por recuperar o último registro salvo caso houver, ou realizar a primeira pesquisa
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param palavra - Palavra Pesquisada
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	public DicionarioJson recuperarPalavra(String palavra) {
		DicionarioJson dicionarioJson = new DicionarioJson();
		List<Dicionario> dicionarios = dao.findAll();		
		
		if(!dicionarios.isEmpty()) {
			dicionarioJson = pecorrerDoUltimoRegistroCadastrado(dicionarios, palavra);
		}else {
			dicionarioJson = pecorrerDoInicio(palavra);
		}
		
		return dicionarioJson;		
	}
	
	/**
	 * Método responsável por recuperar o último registro salvo
	 * 
	 * @author ricardo.ribeiro
	 *	 
	 * @param dicionarios - Lista de dicionarios salvos
	 * @param palavra - Palavra Pesquisada
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	private DicionarioJson pecorrerDoUltimoRegistroCadastrado(List<Dicionario> dicionarios, String palavra) {
		Dicionario ultimo = dicionarios.get(dicionarios.size() - 1);
		
		Integer gatinho = 0;
		UflaJson json = new UflaJson();
		json = consumirServicoDicionario(ultimo.getPosicao() + 1);
		String chaveClient = getChave(json.getPalavra());
		Dicionario dicionario = new Dicionario(chaveClient, ultimo.getPosicao() + 1 , json.getPalavra());
		addDicionario(chaveClient, dicionario);
		
		return pecorrerMelhorPosicao(palavra, ++gatinho, dicionario, json.getStatus());
	}
	
	/**
	 * Método responsável por inciar a primeira busca da palavra
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param palavra - Palavra Pesquisada
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	public DicionarioJson pecorrerDoInicio(String palavra) {
		Integer gatinho = 0;
		UflaJson json = new UflaJson();
		json = consumirServicoDicionario(BigDecimal.ZERO.intValue());
		String chaveClient = getChave(json.getPalavra());
		Dicionario dicionario = new Dicionario(chaveClient, BigDecimal.ZERO.intValue() , json.getPalavra());
		addDicionario(chaveClient, dicionario);
		
		return pecorrerMelhorPosicao(palavra, ++gatinho, dicionario, json.getStatus());
		
	}
	
	/**
	 * Método responsável por encontrar a melhor posição para realizar a consulta do serviço
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param palavraPesquisa - Palavra Pesquisada
	 * @param gatinho - Gatinhos mortos
	 * @param dicionario - Entidade do Dicionario
	 * @param status - Status do serviço
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	public DicionarioJson pecorrerMelhorPosicao(String palavraPesquisa, Integer gatinho, Dicionario dicionario, String status) {
		
		DicionarioJson dicionarioJson = new DicionarioJson();
		Integer melhorPosicao = 0;
		
		if(!palavraPesquisa.equalsIgnoreCase(dicionario.getDescricao())) {			
			String chaveClient = getChave(dicionario.getDescricao());
			String chavePesquisa = getChave(palavraPesquisa);
			
			if(chaveClient.equalsIgnoreCase(chavePesquisa)) {
				dicionarioJson = pecorrerCrescente(dicionario.getPosicao() + 1, palavraPesquisa, gatinho);
			}else if(isCompareString(palavraPesquisa, dicionario.getDescricao())) {
				melhorPosicao = dicionario.getPosicao() == 0 ? dicionario.getPosicao() + 1 : dicionario.getPosicao() * 2;
				dicionarioJson = pecorrerNoDicionario(melhorPosicao, gatinho, palavraPesquisa);				
			}else {
				dicionarioJson = pecorrerDecrescente(dicionario.getPosicao() - 1, palavraPesquisa, gatinho);
			}
		}else {
			dicionarioJson = DicionarioMapper.mapper(dicionario, status, gatinho);
		}
		
		
		return dicionarioJson;
	}
	
	/**
	 * Método responsável por pecorrer no dicionario e salvar na base
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param melhorPosicao - Melhor posição para realizar pesquisa
	 * @param gatinho - Gatinhos mortos
	 * @param palavraPesquisa - Palavra pesquisada	
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	public DicionarioJson pecorrerNoDicionario(Integer melhorPosicao, Integer gatinho, String palavraPesquisa) {
		UflaJson json = new UflaJson();
		json = consumirServicoDicionario(melhorPosicao);
		gatinho++;
		String chaveClient = getChave(json.getPalavra());
		
		Dicionario dicionario = new Dicionario(chaveClient, melhorPosicao, json.getPalavra());
		addDicionario(chaveClient, dicionario);
		
		return pecorrerMelhorPosicao(palavraPesquisa, gatinho, dicionario, json.getStatus());
		
	}
	
	/**
	 * Método recursivo que pecorre em ordem decrescente
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param posicao - Posicao da palavra
	 * @param palavra - Palavra pesquisada
	 * @param gatinho - Gatinhos mortos
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	private DicionarioJson pecorrerDecrescente(Integer posicao, String palavra, Integer gatinho) {
		UflaJson json = new UflaJson();
		DicionarioJson dicionarioJson = new DicionarioJson();
		if(!contemPosicao(posicao) && posicao.compareTo(BigDecimal.ZERO.intValue()) > 0) {
			json = consumirServicoDicionario(posicao);
			gatinho++;			
			String chave = getChave(json.getPalavra());
			Dicionario dicionario = new Dicionario(chave, posicao, json.getPalavra());
			addDicionario(chave, dicionario);
			
			if(isIgual(json.getPalavra(), palavra)) {
				dicionarioJson =  DicionarioMapper.mapper(dicionario, json.getStatus(), gatinho);
			}else {
				dicionarioJson = pecorrerDecrescente(--posicao, palavra, gatinho);
			}
		}else if(posicao.compareTo(BigDecimal.ZERO.intValue()) < 0) {
			dicionarioJson = pecorrerCrescente(++posicao, palavra, gatinho);
		}else {
			dicionarioJson = pecorrerDecrescente(--posicao, palavra, gatinho);
		}
		
		return dicionarioJson;
	}
	
	/**
	 * Método recursivo que pecorre em ordem crescente
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param posicao - Posicao da palavra
	 * @param palavra - Palavra pesquisada
	 * @param gatinho - Gatinhos mortos
	 * 
	 * @return {@link DicionarioJson} - Retorna o Json do Dicionario
	 */
	
	private DicionarioJson pecorrerCrescente(Integer posicao, String palavra, Integer gatinho) {
		UflaJson json = new UflaJson();
		DicionarioJson dicionarioJson = new DicionarioJson();
		
		if(!contemPosicao(posicao)) {
			json = consumirServicoDicionario(posicao);
			gatinho++;			
			String chave = getChave(json.getPalavra());
			Dicionario dicionario = new Dicionario(chave, posicao, json.getPalavra());
			addDicionario(chave, dicionario);
			
			if(isIgual(json.getPalavra(), palavra)) {
				dicionarioJson =  DicionarioMapper.mapper(dicionario, json.getStatus(), gatinho);
			}else {
				dicionarioJson = pecorrerCrescente(++posicao, palavra, gatinho);
			}
		}else {
			dicionarioJson = pecorrerCrescente(++posicao, palavra, gatinho);
		}
		
		return dicionarioJson;
	}	
	
	/**
	 * Método responsável por salvar o dicionario na base
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param chave - Chave da palavra
	 * @param dicionario - Entidade dicionario	 
	 *
	 */
	
	public void addDicionario(String chave, Dicionario dicionario) {
		List<Dicionario> dicionarios = dao.findAllChave(chave);
		if(dicionarios != null && !dicionarios.isEmpty()) {			
			boolean contem = dicionarios.stream().anyMatch(d -> d.getDescricao().equalsIgnoreCase(dicionario.getDescricao()));
			if(!contem) {
				dao.save(dicionario);
			}
		}else {
			dao.save(dicionario);
		}
	}	
	
	/**
	 * Método responsável por recuperar o dicionário na base
	 * 
	 * @author ricardo.ribeiro
	 *	
	 * @param dicionarios - Lista de Dicionarios salvos
	 * @param palavra - Palavra pesquisada	 
	 *
	 * @return {@link Dicionario} - Retorna a Entidade de Dicionario
	 */
	
	private Dicionario recuperarDicionario(List<Dicionario> dicionarios, String palavra) {
		Dicionario dicionario = new Dicionario();
		if(!dicionarios.isEmpty()) {
			Optional<Dicionario> optional =  dicionarios.stream().filter(d -> d.getDescricao().toUpperCase().equals(palavra.toUpperCase())).findFirst();
			
			if(optional.isPresent()) {
				dicionario = optional.get();
			}
		}
		
		return dicionario;
	}
	
	/**
	 * Método responsável por recuperar a chave da palavra
	 * 
	 * @author ricardo.ribeiro	
	 *
	 * @param palavra - Palavra pesquisada	 
	 *
	 * @return {@link String} - Retorna a chave da palavra
	 */
	
	private String getChave(String palavra) {		
		StringBuilder chave = new StringBuilder();
		
		if(palavra != null && palavra.length() > 1) {
			chave.append(Character.toString(palavra.charAt(0)).toUpperCase());
			chave.append(Character.toString(palavra.charAt(1)).toUpperCase());
		}else {
			chave.append(Character.toString(palavra.charAt(0)).toUpperCase());
		}
		
		return chave.toString();
	}
	
	/**
	 * Método responsável por consumir a API de Dicionario
	 * 
	 * @author ricardo.ribeiro
	 *	 
	 * @param posicao - Posição a ser pesquisado	 
	 *
	 * @return {@link UflaJson} - Json do serviço
	 */
	
	public UflaJson consumirServicoDicionario(Integer posicao) {		
		UflaJson json = dicionarioClient.getDicionario(posicao);	
		return json;
	}
	
	/**
	 * Método responsável por verificar posição existente
	 * 
	 * @author ricardo.ribeiro
	 *	 
	 * @param posicao - Posição a ser pesquisado	 
	 *
	 * @return {@link boolean} - TRUE se existir e FALSE não existir 
	 */
	
	private boolean contemPosicao(Integer posicao) {
		return dao.contemPosicao(posicao);
	}
	
	/**
	 * Método responsável por comparar strings se for maior ou menor em ordem alfabética
	 * 
	 * @author ricardo.ribeiro
	 *	 
	 * @param string1 - String 	
	 * @param string2 - String 
	 *
	 * @return {@link boolean} - TRUE se existir e FALSE não existir 
	 */
	
	private boolean isCompareString(String string1, String string2) {
		return string1.compareToIgnoreCase(string2) > 0;
	}
	
	/**
	 * Método responsável por comparar se as palabras são iguais
	 * 
	 * @author ricardo.ribeiro
	 *	 
	 * @param palavraClient - Palavra recuperada da API
	 * @param palavraPesquisa - Palavra pesquisada	  
	 *
	 * @return {@link boolean} - TRUE se existir e FALSE não existir 
	 */
	
	private boolean isIgual(String palavraClient, String palavraPesquisa) {
		return palavraClient.equalsIgnoreCase(palavraPesquisa);
	}
	

}
