package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private MemberPaymentRepository memberPaymentRepo;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Deve retornar NULL se o aluno estiver inativo")
    void shouldReturnNullWhenMemberIsInactive() {
        // Arrange (Preparação)
        Member inativo = new Member();
        inativo.setActive(false);
        LocalDate dataVencimento = LocalDate.of(2026, 3, 5);

        // Act (Ação)
        MemberPayment resultado = paymentService.generateCharge(inativo, dataVencimento);

        // Assert (Verificação)
        assertNull(resultado, "O pagamento não deveria ser gerado para aluno inativo");
        verify(memberPaymentRepo, never()).save(any()); // Garante que não chamou o banco
    }

    @Test
    @DisplayName("Deve gerar a cobrança corretamente para aluno ativo com plano")
    void shouldGenerateChargeSuccessfully() {
        // Arrange
        Plan plano = new Plan();
        plano.setPrice(new BigDecimal("99.90"));

        Member ativo = new Member();
        ativo.setActive(true);
        ativo.setPlan(plano);

        LocalDate dataVencimento = LocalDate.of(2026, 3, 5);

        // Simulando o comportamento do save() do repository
        when(memberPaymentRepo.save(any(MemberPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MemberPayment resultado = paymentService.generateCharge(ativo, dataVencimento);

        // Assert
        assertNotNull(resultado);
        assertEquals(new BigDecimal("99.90"), resultado.getAmountCharged());
        assertEquals(dataVencimento, resultado.getDueDate());
        assertNull(resultado.getPaymentDate()); // Tem que nascer em aberto

        verify(memberPaymentRepo, times(1)).save(any()); // Garante que salvou no banco
    }
}