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

    public TransactionService(TransactionRepository transactionRepo, CategoryService categoryService) {
        this.transactionRepo = transactionRepo;
        this.categoryService = categoryService;
    }

    public Page<TransactionResponseDTO> listTransactions(int month, int year, TransactionType type, Long categoryId, Pageable pageable) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        Long finalCategoryId = (categoryId != null && categoryId > 0) ? categoryId : null;

        Page<Transaction> transactions = transactionRepo.findTransactionsWithFilters(
                start, end, type, finalCategoryId, pageable);

        return transactions.map(TransactionResponseDTO::new);
    }

    public TransactionResponseDTO createTransaction(NewTransactionDTO dados) {
        Category category = categoryService.getCategoryById(dados.categoryId());

        Transaction transaction = new Transaction();
        transaction.setDescription(dados.description());
        transaction.setAmount(dados.amount());
        transaction.setType(dados.transactionType());
        transaction.setPaymentMethod(dados.paymentMethod());
        transaction.setCategory(category);
        transaction.setTransactionDate(LocalDate.now());

        transactionRepo.save(transaction);

        return new TransactionResponseDTO(transaction);
    }

    public Transaction saveMembershipTransaction(MemberPayment payment, @NotNull PaymentMethod paymentMethod) {
        Transaction income = new Transaction();
        income.setDescription("Mensalidade - %s".formatted(payment.getMember().getName()));
        income.setAmount(payment.getAmountCharged());
        income.setPaymentMethod(paymentMethod);
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(LocalDate.now());

        Category category = categoryService.getCategoryByName("Mensalidades");
        income.setCategory(category);

        return transactionRepo.save(income);
    }

    public BigDecimal sumIncomesBetween(LocalDate startDate, LocalDate endDate) {
        return transactionRepo.sumIncomesBetween(startDate, endDate);
    }


    public CashFlowDTO cashFlowResume() {
        BigDecimal totalEntradas = transactionRepo.sumTotalIncomes();
        BigDecimal totalSaidas = transactionRepo.sumTotalExpenses();
        BigDecimal saldoFinal = totalEntradas.subtract(totalSaidas);

        return new CashFlowDTO(totalEntradas, totalSaidas, saldoFinal);
    }
}
