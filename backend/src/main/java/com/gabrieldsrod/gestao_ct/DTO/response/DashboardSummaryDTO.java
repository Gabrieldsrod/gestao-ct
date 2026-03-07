package com.gabrieldsrod.gestao_ct.DTO.response;

import java.util.List;

public record DashboardSummaryDTO(
        CashFlowDTO finance,
        long activeMembers,
        long delinquentMembers,
        long pendingMembers,
        List<MonthlyChartDataDTO> chartData
) {

}
