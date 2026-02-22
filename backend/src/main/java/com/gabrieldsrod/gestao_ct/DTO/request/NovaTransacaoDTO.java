package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.MetodoPagamento;
import com.gabrieldsrod.gestao_ct.Enums.TipoTransacao;

import java.math.BigDecimal;

public record NovaTransacaoDTO(
        String descricao,
        BigDecimal valor,
        TipoTransacao tipoTransacao,
        MetodoPagamento metodoPagamento,
        Long categoriaId
) {
}
