package com.gabrieldsrod.gestao_ct.DTO.response;

import java.math.BigDecimal;

public record MonthlyChartDataDTO(
        String month,
        BigDecimal revenue,
        long activeMembers
) {
}
