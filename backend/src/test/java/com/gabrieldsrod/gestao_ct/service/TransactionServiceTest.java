package com.gabrieldsrod.gestao_ct.service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewTransactionDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.TransactionResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.TransactionRepository;
import com.gabrieldsrod.gestao_ct.Service.CategoryService;
import com.gabrieldsrod.gestao_ct.Service.FeeService;
import com.gabrieldsrod.gestao_ct.Service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private FeeService feeService;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    @DisplayName("Deve criar uma transação manual com sucesso")
    void shouldCreateTransactionSuccessfully() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Material");
        // Arrange
        NewTransactionDTO dto = new NewTransactionDTO(
                LocalDate.now(),
                "Compra de material",
                new BigDecimal("250.00"),
                TransactionType.EXPENSE,
                PaymentMethod.CASH,
                1L
        );

        when(categoryService.getCategoryById(1L)).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> {
            Transaction savedTransaction = invocation.getArgument(0);
            savedTransaction.setId(10L);
            return savedTransaction;
        });

        // Act
        TransactionResponseDTO result = transactionService.createTransaction(dto);

        // Assert
        assertNotNull(result);
        assertEquals(10L, result.id());
        assertEquals("Compra de material", result.description());
        assertEquals(new BigDecimal("250.00"), result.netAmount());
        assertEquals(TransactionType.EXPENSE.name(), result.transactionType());
        verify(categoryService, times(1)).getCategoryById(1L);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve salvar uma transação de mensalidade com cálculo de taxa")
    void shouldSaveMembershipTransactionWithFeeCalculation() {
        // Arrange
        Member member = new Member();
        member.setName("João Aluno");
        MemberPayment payment = new MemberPayment();
        payment.setMember(member);
        payment.setAmountCharged(new BigDecimal("100.00"));

        Category category = new Category();
        category.setName("Mensalidades");

        when(feeService.calculateFee(PaymentMethod.CREDIT_CARD, new BigDecimal("100.00"))).thenReturn(new BigDecimal("5.00"));
        when(categoryService.getCategoryByName("Mensalidades")).thenReturn(category);
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Transaction result = transactionService.saveMembershipTransaction(payment, PaymentMethod.CREDIT_CARD);

        // Assert
        assertNotNull(result);
        assertEquals("Mensalidade - João Aluno", result.getDescription());
        assertEquals(new BigDecimal("100.00"), result.getGrossAmount());
        assertEquals(new BigDecimal("5.00"), result.getFeeAmount());
        assertEquals(new BigDecimal("95.00"), result.getNetAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals(category, result.getCategory());
        verify(feeService, times(1)).calculateFee(PaymentMethod.CREDIT_CARD, new BigDecimal("100.00"));
        verify(categoryService, times(1)).getCategoryByName("Mensalidades");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    @DisplayName("Deve retornar o fluxo de caixa para um mês e ano específicos")
    void shouldGetCashFlowForSpecificMonthAndYear() {
        // Arrange
        int month = 3;
        int year = 2024;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        when(transactionRepository.sumAmountByPeriodAndType(start, end, TransactionType.INCOME)).thenReturn(new BigDecimal("5000.00"));
        when(transactionRepository.sumAmountByPeriodAndType(start, end, TransactionType.EXPENSE)).thenReturn(new BigDecimal("1500.00"));

        // Act
        CashFlowDTO result = transactionService.getCashFlow(month, year);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("5000.00"), result.totalIncome());
        assertEquals(new BigDecimal("1500.00"), result.totalExpenses());
        assertEquals(new BigDecimal("3500.00"), result.netBalance());
    }

    @Test
    @DisplayName("Deve retornar o resumo geral do fluxo de caixa")
    void shouldGetCashFlowResume() {
        // Arrange
        when(transactionRepository.sumTotalAmountByType(TransactionType.INCOME)).thenReturn(new BigDecimal("100000.00"));
        when(transactionRepository.sumTotalAmountByType(TransactionType.EXPENSE)).thenReturn(new BigDecimal("40000.00"));

        // Act
        CashFlowDTO result = transactionService.cashFlowResume();

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("100000.00"), result.totalIncome());
        assertEquals(new BigDecimal("40000.00"), result.totalExpenses());
        assertEquals(new BigDecimal("60000.00"), result.netBalance());
    }

    @Test
    @DisplayName("Deve listar transações com filtros")
    void shouldListTransactionsWithFilters() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        int month = 3;
        int year = 2024;
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        Transaction transaction = new Transaction();
        transaction.setId(1L);
        transaction.setDescription("Teste");
        transaction.setCategory(new Category());
        transaction.setType(TransactionType.INCOME);
        transaction.setPaymentMethod(PaymentMethod.CASH);
        transaction.setTransactionDate(LocalDate.of(year, month, 15));
        Page<Transaction> transactionPage = new PageImpl<>(java.util.List.of(transaction));

        when(transactionRepository.findTransactionsWithFilters(start, end, TransactionType.INCOME, 1L, pageable))
                .thenReturn(transactionPage);

        // Act
        Page<TransactionResponseDTO> result = transactionService.listTransactions(month, year, TransactionType.INCOME, 1L, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals("Teste", result.getContent().getFirst().description());
        verify(transactionRepository, times(1)).findTransactionsWithFilters(start, end, TransactionType.INCOME, 1L, pageable);
    }
}
