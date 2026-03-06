package com.gabrieldsrod.gestao_ct.DTO.response;

import java.math.BigDecimal;

public record CashFlowDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance
) {
}
