package com.gabrieldsrod.gestao_ct.DTO.response;

import java.math.BigDecimal;

public record ReciboPagamentoDTO(
        Long id,
        String nome,
        String status
) {}
