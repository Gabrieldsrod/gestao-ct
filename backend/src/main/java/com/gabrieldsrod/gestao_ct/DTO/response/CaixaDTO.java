package com.gabrieldsrod.gestao_ct.DTO.response;

import java.math.BigDecimal;
import java.util.List;

public record CaixaDTO(
        BigDecimal totalEntradas,
        BigDecimal totalSaidas,
        BigDecimal saldoAtual,
        List<TransacaoDTO> ultimasTransacoes
) {
}
