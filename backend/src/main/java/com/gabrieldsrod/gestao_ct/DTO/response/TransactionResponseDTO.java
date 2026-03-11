package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;

import java.math.BigDecimal;

public record TransactionResponseDTO(
        Long id,
        String description,
        String category,
        String transactionType,
        String paymentMethod,
        String transactionDate,
        BigDecimal amount
) {
    public TransactionResponseDTO(Transaction transaction) {
        this(transaction.getId(),
                transaction.getDescription(),
                transaction.getCategory().getName(),
                transaction.getType().name(),
                transaction.getPaymentMethod().name(),
                transaction.getTransactionDate().format(DateUtils.BR_FORMATTER_DATE),
                transaction.getAmount());
    }
}
