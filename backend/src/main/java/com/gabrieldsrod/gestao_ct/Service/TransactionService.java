package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewTransactionDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.TransactionResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
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

    private final CategoryRepository categoryRepo;

    public TransactionService(TransactionRepository transactionRepo, CategoryRepository categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    public Page<TransactionResponseDTO> list(int month, int year, Pageable pageable) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return transactionRepo.findByTransactionDateBetween(start, end, pageable).map(TransactionResponseDTO::new);
    }

    public TransactionResponseDTO create(NewTransactionDTO dados) {
        Category category = categoryRepo.findById(dados.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

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
