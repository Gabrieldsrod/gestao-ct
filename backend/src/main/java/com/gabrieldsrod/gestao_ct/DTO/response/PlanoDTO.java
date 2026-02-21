package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Plano;

import java.math.BigDecimal;

public record PlanoDTO(
        Long id,
        String nome,
        BigDecimal valorMensalidade
) {
    public PlanoDTO(Plano plano) {
        this(plano.getId(), plano.getNome(), plano.getValorMensalidade());
    }
}
