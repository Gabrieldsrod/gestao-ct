package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import com.gabrieldsrod.gestao_ct.Service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private MemberPaymentRepository memberPaymentRepo;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private PaymentService paymentService;

    @Test
    @DisplayName("Deve retornar NULL se o aluno estiver inativo")
    void shouldReturnNullWhenMemberIsInactive() {
        // Arrange
        Member inactiveMember = new Member();
        inactiveMember.setStatus(MemberStatus.INACTIVE);
        LocalDate dueDate = LocalDate.of(2026, 3, 5);

        // Act
        MemberPayment result = paymentService.generateCharge(inactiveMember, dueDate);

        // Assert
        assertNull(result, "O pagamento não deveria ser gerado para aluno inativo");
        verify(memberPaymentRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve gerar a cobrança corretamente para aluno ativo com plano")
    void shouldGenerateChargeSuccessfully() {
        // Arrange
        Plan plan = new Plan();
        plan.setPrice(new BigDecimal("99.90"));

        Member activeMember = new Member();
        activeMember.setStatus(MemberStatus.ACTIVE);
        activeMember.setPlan(plan);

        LocalDate dueDate = LocalDate.of(2026, 3, 5);

        when(memberPaymentRepo.existsByMemberAndMonthAndYear(activeMember, dueDate.getMonthValue(), dueDate.getYear())).thenReturn(false);
        when(memberPaymentRepo.save(any(MemberPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        MemberPayment result = paymentService.generateCharge(activeMember, dueDate);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("99.90"), result.getAmountCharged());
        assertEquals(dueDate, result.getDueDate());
        assertNull(result.getPaymentDate());
        assertEquals(PaymentStatus.PENDING, result.getStatus());
        verify(memberPaymentRepo, times(1)).save(any(MemberPayment.class));
    }

    @Test
    @DisplayName("Não deve gerar cobrança se já existir uma para o mesmo mês/ano")
    void shouldNotGenerateChargeIfOneAlreadyExists() {
        // Arrange
        Plan plan = new Plan();
        plan.setPrice(new BigDecimal("99.90"));

        Member member = new Member();
        member.setStatus(MemberStatus.ACTIVE);
        member.setPlan(plan);

        LocalDate dueDate = LocalDate.of(2026, 3, 5);

        when(memberPaymentRepo.existsByMemberAndMonthAndYear(member, dueDate.getMonthValue(), dueDate.getYear())).thenReturn(true);

        // Act
        MemberPayment result = paymentService.generateCharge(member, dueDate);

        // Assert
        assertNull(result);
        verify(memberPaymentRepo, never()).save(any());
    }

    @Test
    @DisplayName("Deve registrar um pagamento com sucesso")
    void shouldRegisterPaymentSuccessfully() {
        // Arrange
        Long paymentId = 1L;
        Member member = new Member();
        member.setName("Test Member");
        member.setStatus(MemberStatus.PENDING);

        MemberPayment payment = new MemberPayment();
        payment.setId(paymentId);
        payment.setMember(member);
        payment.setStatus(PaymentStatus.PENDING);
        payment.setAmountCharged(new BigDecimal("100.00"));

        Transaction transaction = new Transaction();
        transaction.setId(100L);

        when(memberPaymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));
        when(transactionService.saveMembershipTransaction(any(MemberPayment.class), any(PaymentMethod.class))).thenReturn(transaction);
        when(memberPaymentRepo.save(any(MemberPayment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PaymentReceiptDTO receipt = paymentService.registerPayment(paymentId, PaymentMethod.PIX);

        // Assert
        assertNotNull(receipt);
        assertEquals(paymentId, receipt.id());
        assertEquals("Test Member", receipt.name());
        assertEquals(PaymentStatus.PAID.name(), receipt.status());

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(MemberStatus.ACTIVE, member.getStatus());
        assertNotNull(payment.getPaymentDate());
        assertNotNull(payment.getTransaction());
        verify(memberPaymentRepo, times(1)).findById(paymentId);
        verify(transactionService, times(1)).saveMembershipTransaction(payment, PaymentMethod.PIX);
        verify(memberPaymentRepo, times(1)).save(payment);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar cobrança inexistente")
    void shouldThrowExceptionWhenPayingNonExistentPayment() {
        // Arrange
        Long paymentId = 99L;
        when(memberPaymentRepo.findById(paymentId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> paymentService.registerPayment(paymentId, PaymentMethod.CREDIT_CARD));

        assertEquals("Pagamento não encontrado", exception.getMessage());
        verify(transactionService, never()).saveMembershipTransaction(any(), any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar pagar uma cobrança já paga")
    void shouldThrowExceptionWhenPayingAlreadyPaidPayment() {
        // Arrange
        Long paymentId = 1L;
        MemberPayment payment = new MemberPayment();
        payment.setId(paymentId);
        payment.setStatus(PaymentStatus.PAID);

        when(memberPaymentRepo.findById(paymentId)).thenReturn(Optional.of(payment));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> paymentService.registerPayment(paymentId, PaymentMethod.PIX));

        assertEquals("Pagamento já registrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve cancelar todas as cobranças pendentes de um aluno")
    void shouldCancelAllPendingChargesForAMember() {
        // Arrange
        Member member = new Member();
        member.setId(1L);

        MemberPayment pendingPayment1 = new MemberPayment();
        pendingPayment1.setStatus(PaymentStatus.PENDING);
        MemberPayment pendingPayment2 = new MemberPayment();
        pendingPayment2.setStatus(PaymentStatus.PENDING);
        List<MemberPayment> pendingPayments = List.of(pendingPayment1, pendingPayment2);

        when(memberPaymentRepo.findByMemberAndStatus(member, PaymentStatus.PENDING)).thenReturn(pendingPayments);

        // Act
        paymentService.cancelPendingCharges(member);

        // Assert
        assertEquals(PaymentStatus.CANCELED, pendingPayment1.getStatus());
        assertEquals(PaymentStatus.CANCELED, pendingPayment2.getStatus());
        verify(memberPaymentRepo, times(2)).save(any(MemberPayment.class));
    }

    @Test
    @DisplayName("Deve atualizar o valor das cobranças pendentes ao mudar de plano")
    void shouldUpdatePendingChargesAmountOnPlanChange() {
        // Arrange
        Member member = new Member();
        member.setId(1L);
        BigDecimal newPrice = new BigDecimal("150.00");

        MemberPayment pendingPayment = new MemberPayment();
        pendingPayment.setAmountCharged(new BigDecimal("100.00"));
        pendingPayment.setStatus(PaymentStatus.PENDING);
        List<MemberPayment> pendingPayments = List.of(pendingPayment);

        when(memberPaymentRepo.findByMemberAndStatus(member, PaymentStatus.PENDING)).thenReturn(pendingPayments);

        // Act
        paymentService.updatePendingChargesForPlanChange(member, newPrice);

        // Assert
        assertEquals(newPrice, pendingPayment.getAmountCharged());
        verify(memberPaymentRepo, times(1)).save(pendingPayment);
    }
}
