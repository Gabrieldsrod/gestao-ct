package com.gabrieldsrod.gestao_ct.DTO.request;

import java.math.BigDecimal;

public record FeeUpdateDTO(
        BigDecimal percentageFee,
        BigDecimal fixedFee,
        Integer daysToReceive
) {
}
