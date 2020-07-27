package com.ufla.dicionario.json;

import java.io.Serializable;

public class DicionarioJson implements Serializable {

	private static final long serialVersionUID = 8067715441980129333L;
	
	private Integer posicao;
	
	private Integer gatinho;
	
	private String mensagem;

	public Integer getPosicao() {
		return posicao;
	}

	public void setPosicao(Integer posicao) {
		this.posicao = posicao;
	}

	public Integer getGatinho() {
		return gatinho;
	}

	public void setGatinho(Integer gatinho) {
		this.gatinho = gatinho;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

}
