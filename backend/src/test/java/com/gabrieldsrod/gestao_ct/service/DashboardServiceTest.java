package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.DashboardSummaryDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Service.DashboardService;
import com.gabrieldsrod.gestao_ct.Service.MemberService;
import com.gabrieldsrod.gestao_ct.Service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private TransactionService transactionService;

    @Mock
    private MemberService memberService;

    @InjectMocks
    private DashboardService dashboardService;

    @Test
    @DisplayName("Deve retornar o resumo do dashboard com dados financeiros e de membros")
    void shouldReturnDashboardSummary() {
        // Arrange
        CashFlowDTO cashFlowMock = new CashFlowDTO(
                new BigDecimal("5000.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("4000.00")
        );
        when(transactionService.cashFlowResume()).thenReturn(cashFlowMock);

        when(memberService.countByStatus(MemberStatus.ACTIVE)).thenReturn(50L);
        when(memberService.countByStatus(MemberStatus.DELINQUENT)).thenReturn(5L);
        when(memberService.countByStatus(MemberStatus.PENDING)).thenReturn(10L);

        // Simulando dados para o gráfico (6 meses)
        when(transactionService.sumIncomesBetween(any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(new BigDecimal("1500.00"));
        when(memberService.countActiveMembersUpTo(any(LocalDate.class)))
                .thenReturn(55L);

        // Act
        DashboardSummaryDTO result = dashboardService.getDashboardSummary();

        // Assert
        assertNotNull(result);
        assertEquals(cashFlowMock, result.finance());
        assertEquals(50L, result.activeMembers());
        assertEquals(5L, result.delinquentMembers());
        assertEquals(10L, result.pendingMembers());

        // Verifica os dados do gráfico (devem ser 6 meses gerados)
        assertNotNull(result.chartData());
        assertEquals(6, result.chartData().size());

        // O último elemento deve ser o mês atual com os mocks definidos
        assertEquals(new BigDecimal("1500.00"), result.chartData().get(5).revenue());
        assertEquals(55L, result.chartData().get(5).activeMembers());

        verify(transactionService, times(1)).cashFlowResume();
        verify(memberService, times(1)).countByStatus(MemberStatus.ACTIVE);
        verify(memberService, times(1)).countByStatus(MemberStatus.DELINQUENT);
        verify(memberService, times(1)).countByStatus(MemberStatus.PENDING);
        verify(transactionService, times(6)).sumIncomesBetween(any(LocalDate.class), any(LocalDate.class));
        verify(memberService, times(6)).countActiveMembersUpTo(any(LocalDate.class));
    }
}
