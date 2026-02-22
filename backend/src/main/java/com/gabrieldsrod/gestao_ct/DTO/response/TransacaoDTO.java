package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;
import com.gabrieldsrod.gestao_ct.Model.Transacao;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;

import java.math.BigDecimal;

public record TransacaoDTO(
        Long id,
        String descricao,
        String categoria,
        String tipoTransacao,
        String metodoPagamento,
        String dataMovimento,
        BigDecimal valor
) {
    public TransacaoDTO(Transacao transacao) {
        this(transacao.getId(),
                transacao.getDescricao(),
                transacao.getCategoria().getNome(),
                transacao.getTipo().name(),
                transacao.getMetodoPagamento().name(),
                transacao.getDataMovimento().format(DateUtils.BR_FORMATTER),
                transacao.getValor());
    }
}
