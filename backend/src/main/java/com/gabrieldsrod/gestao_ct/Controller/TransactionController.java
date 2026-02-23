package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.NewTransactionDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.CashFlowDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.TransactionDTO;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import com.gabrieldsrod.gestao_ct.Repository.TransactionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionRepository transactionRepo;

    private final CategoryRepository categoryRepo;

    public TransactionController(TransactionRepository transactionRepo, CategoryRepository categoryRepo) {
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoryRepo;
    }

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestBody NewTransactionDTO dados) {
        Category category = categoryRepo.findById(dados.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Transaction newTransaction = new Transaction();
        newTransaction.setDescription(dados.description());
        newTransaction.setAmount(dados.amount());
        newTransaction.setType(dados.transactionType());
        newTransaction.setPaymentMethod(dados.paymentMethod());
        newTransaction.setCategory(category);
        newTransaction.setTransactionDate(LocalDate.now());

        transactionRepo.save(newTransaction);

        return ResponseEntity.ok().body(Map.of(
                "id", newTransaction.getId(),
                "descricao", newTransaction.getDescription(),
                "categoria", category.getName(),
                "valor", newTransaction.getAmount()
        ));
    }

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> listarTransacoes() {
        try {
            List<TransactionDTO> transacoes = transactionRepo.findAllByOrderByTransactionDateDesc().stream()
                    .map(TransactionDTO::new)
                    .toList();
            return ResponseEntity.ok(transacoes);
        }
        catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/caixa")
    public ResponseEntity<?> obterResumoCaixa() {

        List<Transaction> transacoes = transactionRepo.findAll();

        BigDecimal totalEntradas = transacoes.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSaidas = transacoes.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal saldoFinal = totalEntradas.subtract(totalSaidas);

        List<TransactionDTO> listaTransacoesDto = transacoes.stream()
                .map(TransactionDTO::new)
                .toList();

        CashFlowDTO resumo = new CashFlowDTO(totalEntradas, totalSaidas, saldoFinal, listaTransacoesDto);

        return ResponseEntity.ok(resumo);
    }
}
