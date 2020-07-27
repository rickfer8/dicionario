package com.ufla.dicionario.service;

import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ufla.dicionario.dao.DicionarioDAO;
import com.ufla.dicionario.entidade.Dicionario;
import com.ufla.dicionario.integracao.DicionarioClient;
import com.ufla.dicionario.json.UflaJson;
import com.ufla.dicionario.mapper.DicionarioMapper;

public class DicionarioServiceTest {
	
	@InjectMocks
	private DicionarioService dicionarioService;
	
	@Mock
	private DicionarioClient dicionarioClient;
	
	@Mock
	private DicionarioMapper mapper;
	
	private static String PALAVRA_PESQUISA_ZERO = "Achafundar";
	private static String PALAVRA_PESQUISA_UM = "Abacamartado";
	private static String PALAVRA_PESQUISA_DOIS = "Abafo";
	private static String CHAVE_ZERO = "AC";
	private static String CHAVE_UM = "AB";
	private static String STATUS = "OK";
	
	@Mock
	private DicionarioDAO dao;
	
	private Dicionario mockDicionarioZero() {
		Dicionario dicionario = new Dicionario();
		dicionario.setId(1l);
		dicionario.setPosicao(0);
		dicionario.setChave(CHAVE_ZERO);
		dicionario.setDescricao(PALAVRA_PESQUISA_ZERO);		
		return dicionario;
	}
	
	private Dicionario mockDicionarioUm() {
		Dicionario dicionario = new Dicionario();
		dicionario.setId(2l);
		dicionario.setPosicao(1);
		dicionario.setChave(CHAVE_UM);
		dicionario.setDescricao(PALAVRA_PESQUISA_UM);		
		return dicionario;
	}
	
	private UflaJson mockUflaJsonPosicaoZero() {
		UflaJson json = new UflaJson();		
		json.setPalavra("Achafundar");
		json.setStatus("OK");
		return json;
	}
	
	private UflaJson mockUflaJsonPosicaoUm() {
		UflaJson json = new UflaJson();		
		json.setPalavra("Abacamartado");
		json.setStatus("OK");
		return json;
	}
	
	private UflaJson mockUflaJsonPosicaoDois() {
		UflaJson json = new UflaJson();		
		json.setPalavra("Abafo");
		json.setStatus("OK");
		return json;
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void pecorrerDicionarioTest() {
		UflaJson jsonUflaZero = mockUflaJsonPosicaoZero();
		UflaJson jsonUflaUm = mockUflaJsonPosicaoUm();
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ZERO.intValue())).thenReturn(jsonUflaZero);
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ONE.intValue())).thenReturn(jsonUflaUm);		
		assertNotNull(dicionarioService.pecorrerDicionario(PALAVRA_PESQUISA_UM));
	}
	
	@Test
	public void pecorrerDicionarioContemNaBaseTest() {
		UflaJson jsonUflaUm = mockUflaJsonPosicaoUm();
		Mockito.when(dao.findAllChave(CHAVE_UM)).thenReturn(Arrays.asList(mockDicionarioUm()));
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ONE.intValue())).thenReturn(jsonUflaUm);
		assertNotNull(dicionarioService.pecorrerDicionario(PALAVRA_PESQUISA_UM));
	}
	
	@Test
	public void recuperarPalavraTest() {		
		UflaJson jsonUflaUm = mockUflaJsonPosicaoUm();
		Mockito.when(dao.findAll()).thenReturn(Arrays.asList(mockDicionarioZero()));
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ONE.intValue())).thenReturn(jsonUflaUm);
		assertNotNull(dicionarioService.recuperarPalavra(PALAVRA_PESQUISA_UM));
	}
	
	@Test
	public void pecorrerMelhorPosicaoTest() {		
		assertNotNull(dicionarioService.pecorrerMelhorPosicao(PALAVRA_PESQUISA_UM, BigDecimal.ONE.intValue(), mockDicionarioUm(), STATUS));
	}
	
	@Test
	public void pecorrerMelhorPosicaoPalavraIguaisTest() {
		UflaJson jsonUflaDois = mockUflaJsonPosicaoDois();
		Mockito.when(dao.contemPosicao(BigDecimal.ZERO.intValue())).thenReturn(Boolean.TRUE);
		Mockito.when(dicionarioService.consumirServicoDicionario(2)).thenReturn(jsonUflaDois);
		assertNotNull(dicionarioService.pecorrerMelhorPosicao(PALAVRA_PESQUISA_DOIS, BigDecimal.ONE.intValue(), mockDicionarioUm(), STATUS));
	}
	
	@Test
	public void pecorrerNoDicionarioTest() {
		UflaJson jsonUflaUm = mockUflaJsonPosicaoUm();
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ONE.intValue())).thenReturn(jsonUflaUm);
		assertNotNull(dicionarioService.pecorrerNoDicionario(BigDecimal.ONE.intValue(), BigDecimal.ONE.intValue(), PALAVRA_PESQUISA_UM));
	}
	
	@Test
	public void addDicionarioTest() {		
		Mockito.when(dao.findAllChave(CHAVE_UM)).thenReturn(Arrays.asList(mockDicionarioUm()));		
		dicionarioService.addDicionario(CHAVE_UM, mockDicionarioZero());		
	}
	
	@Test
	public void getPosicaoTest() {
		UflaJson jsonUflaUm = mockUflaJsonPosicaoUm();
		Mockito.when(dicionarioService.consumirServicoDicionario(BigDecimal.ONE.intValue())).thenReturn(jsonUflaUm);
		Mockito.when(dao.findAllChave(CHAVE_UM)).thenReturn(Arrays.asList(mockDicionarioUm()));
		assertNotNull(dicionarioService.getPosicao(PALAVRA_PESQUISA_UM));	
	}


}
