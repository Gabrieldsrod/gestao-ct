package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Plan;

import java.math.BigDecimal;

public record PlanResponseDTO(
        Long id,
        String name,
        BigDecimal price,
        String lastUpdated
) {
    public PlanResponseDTO(Plan plan) {
        this(plan.getId(), plan.getName(), plan.getPrice(), plan.getUpdatedAt().toString());
    }
}
