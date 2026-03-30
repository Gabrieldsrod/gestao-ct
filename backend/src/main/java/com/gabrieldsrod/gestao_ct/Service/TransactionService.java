package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewTransactionDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.TransactionResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.TransactionRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;

    private final CategoryService categoryService;

    private final FeeService feeService;

    public TransactionService(TransactionRepository transactionRepo, CategoryService categoryService, FeeService feeService) {
        this.transactionRepo = transactionRepo;
        this.categoryService = categoryService;
        this.feeService = feeService;
    }

    public Page<TransactionResponseDTO> listTransactions(int month, int year, TransactionType type, Long categoryId, Pageable pageable) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Long finalCategoryId = (categoryId != null && categoryId > 0) ? categoryId : null;

        Page<Transaction> transactions = transactionRepo.findTransactionsWithFilters(
                start, end, type, finalCategoryId, pageable);

        return transactions.map(TransactionResponseDTO::new);
    }

    public TransactionResponseDTO createTransaction(NewTransactionDTO data) {
        Category category = categoryService.getCategoryById(data.categoryId());

        Transaction transaction = new Transaction();
        transaction.setDescription(data.description());
        transaction.setPaymentMethod(data.paymentMethod());

        BigDecimal feeAmount = feeService.calculateFee(data.paymentMethod(), data.amount());

        transaction.setGrossAmount(data.amount());
        transaction.setFeeAmount(feeAmount);
        transaction.setNetAmount(data.amount().subtract(feeAmount));
        transaction.setType(data.transactionType());
        transaction.setCategory(category);
        transaction.setTransactionDate(data.transactionDate());

        transactionRepo.save(transaction);

        return new TransactionResponseDTO(transaction);
    }

    public Transaction saveMembershipTransaction(MemberPayment payment, @NotNull PaymentMethod paymentMethod) {
        Transaction income = new Transaction();
        income.setDescription("Mensalidade - %s".formatted(payment.getMember().getName()));
        income.setGrossAmount(payment.getAmountCharged());

        BigDecimal feeAmount = feeService.calculateFee(paymentMethod, income.getGrossAmount());
        income.setFeeAmount(feeAmount);
        income.setNetAmount(income.getGrossAmount().subtract(feeAmount));
        income.setPaymentMethod(paymentMethod);
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(LocalDate.now());

        Category category = categoryService.getCategoryByName("Mensalidades");
        income.setCategory(category);

        return transactionRepo.save(income);
    }

    public BigDecimal sumIncomesBetween(LocalDate start, LocalDate end) {
        return transactionRepo.sumAmountByPeriodAndType(start, end, TransactionType.INCOME);
    }

    public CashFlowDTO getCashFlow(Integer month, Integer year) {
        if (month == null || year == null) {
            return this.cashFlowResume();
        }
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        BigDecimal totalIncomes = transactionRepo.sumAmountByPeriodAndType(start, end,TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepo.sumAmountByPeriodAndType(start, end, TransactionType.EXPENSE);
        BigDecimal finalBalance = totalIncomes.subtract(totalExpenses);

        return new CashFlowDTO(totalIncomes, totalExpenses, finalBalance);
    }

    public CashFlowDTO cashFlowResume() {
        BigDecimal totalIncomes = transactionRepo.sumTotalAmountByType(TransactionType.INCOME);
        BigDecimal totalExpenses = transactionRepo.sumTotalAmountByType(TransactionType.EXPENSE);
        BigDecimal finalBalance = totalIncomes.subtract(totalExpenses);

        return new CashFlowDTO(totalIncomes, totalExpenses, finalBalance);
    }
}
