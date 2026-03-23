package com.gabrieldsrod.gestao_ct.DTO.request;

import java.math.BigDecimal;

public record NewPlanDTO(
        String name,
        BigDecimal price
) {
}
