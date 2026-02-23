package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;

import java.math.BigDecimal;

public record NewTransactionDTO(
        String description,
        BigDecimal amount,
        TransactionType transactionType,
        PaymentMethod paymentMethod,
        Long categoryId
) {
}
