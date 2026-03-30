package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.PaymentFee;

import java.math.BigDecimal;

public record FeeResponseDTO(
        Long id,
        String paymentMethod,
        BigDecimal percentageFee,
        BigDecimal fixedFee,
        Integer daysToReceive
) {
    public FeeResponseDTO(PaymentFee fee) {
        this(fee.getId(),
                fee.getPaymentMethod().name(),
                fee.getPercentageFee(),
                fee.getFixedFee(),
                fee.getDaysToReceive());
    }
}
