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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepo;

    private final CategoryService categoryService;

    public TransactionService(TransactionRepository transactionRepo, CategoryService categoryService) {
        this.transactionRepo = transactionRepo;
        this.categoryService = categoryService;
    }

    public Page<TransactionResponseDTO> listTransactions(int month, int year, Pageable pageable) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return transactionRepo.findByTransactionDateBetween(start, end, pageable).map(TransactionResponseDTO::new);
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

    public Transaction saveMembershipTransaction(MemberPayment payment, PaymentMethod paymentMethod) {
        Transaction income = new Transaction();
        income.setDescription("Mensalidade - " + payment.getMember().getName());
        income.setAmount(payment.getAmountCharged());
        income.setPaymentMethod(paymentMethod);
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(LocalDate.now());

        Category category = categoryService.getCategoryByName("Mensalidade");
        income.setCategory(category);

        return transactionRepo.save(income);
    }


    public CashFlowDTO cashFlowResume() {
        var transactions = transactionRepo.findAll();

        var totalEntradas = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var totalSaidas = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = totalEntradas.subtract(totalSaidas);

        List<TransactionResponseDTO> listaTransacoesDto = transactions.stream()
                .map(TransactionResponseDTO::new)
                .toList();

        return new CashFlowDTO(totalEntradas, totalSaidas, saldoFinal, listaTransacoesDto);
    }

}
