package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Plan;

import java.math.BigDecimal;

public record PlanDTO(
        Long id,
        String name,
        BigDecimal price
) {
    public PlanDTO(Plan plan) {
        this(plan.getId(), plan.getName(), plan.getPrice());
    }
}
