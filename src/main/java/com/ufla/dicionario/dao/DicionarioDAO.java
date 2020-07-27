package com.ufla.dicionario.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ufla.dicionario.entidade.Dicionario;

@Repository
public interface DicionarioDAO extends JpaRepository<Dicionario, Long> {
	
	@Query("select d from Dicionario d where d.chave = :chave order by d.posicao")
	public List<Dicionario> findAllChave(String chave);
	
	@Query("select case when count(d)> 0 then true else false end from Dicionario d where d.posicao = :posicao ")
	public boolean contemPosicao(Integer posicao);

}
