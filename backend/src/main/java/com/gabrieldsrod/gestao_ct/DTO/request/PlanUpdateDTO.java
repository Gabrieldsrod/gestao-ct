package com.gabrieldsrod.gestao_ct.DTO.request;

import java.math.BigDecimal;

public record PlanUpdateDTO(
        String name,
        BigDecimal price
) {
}
