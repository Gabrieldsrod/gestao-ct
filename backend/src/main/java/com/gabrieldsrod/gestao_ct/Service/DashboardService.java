package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.DashboardSummaryDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MonthlyChartDataDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class DashboardService {

    private final TransactionService transactionService;

    private final MemberService memberService;

    public DashboardService(TransactionService transactionService, MemberService memberService) {
        this.transactionService = transactionService;
        this.memberService = memberService;
    }

    public DashboardSummaryDTO getDashboardSummary() {
        CashFlowDTO financeData = transactionService.cashFlowResume();
        long activeMembers = memberService.countByStatus(MemberStatus.ACTIVE);
        long delinquentMembers = memberService.countByStatus(MemberStatus.DELINQUENT);
        long pendingMembers = memberService.countByStatus(MemberStatus.PENDING);

        List<MonthlyChartDataDTO> chartData = generateChartData();

        return new DashboardSummaryDTO(financeData, activeMembers, delinquentMembers, pendingMembers, chartData);
    }

    private List<MonthlyChartDataDTO> generateChartData() {
        int monthsToLookBack = 6;
        List<MonthlyChartDataDTO> chartData = new ArrayList<>();

        Locale ptBR = Locale.of("pt", "BR");

        YearMonth currentMonth = YearMonth.now();

        for (int i = monthsToLookBack - 1; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);

            LocalDate startDate = targetMonth.atDay(1);
            LocalDate endDate = targetMonth.atEndOfMonth();

            String monthName = targetMonth.getMonth().getDisplayName(TextStyle.SHORT, ptBR);
            monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1).replace(".", "");

            BigDecimal revenue = transactionService.sumIncomesBetween(startDate, endDate);
            long students = memberService.countActiveMembersUpTo(endDate);

            chartData.add(new MonthlyChartDataDTO(monthName, revenue, students));
        }

        return chartData;
    }
}
