package com.gabrieldsrod.gestao_ct.DTO.response;

import java.math.BigDecimal;
import java.util.List;

public record CashFlowDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpenses,
        BigDecimal netBalance,
        List<TransactionResponseDTO> lastTransactions
) {
}
