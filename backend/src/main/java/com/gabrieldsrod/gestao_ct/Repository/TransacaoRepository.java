package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByDataMovimentoBetween(LocalDateTime inicio, LocalDateTime fim);

    List<Transacao> findByTipo(TipoTransacao tipo);

    List<Transacao> findAllByOrderByDataMovimentoDesc();
}
