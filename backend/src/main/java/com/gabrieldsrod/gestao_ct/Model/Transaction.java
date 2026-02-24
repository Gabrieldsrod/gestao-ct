package com.gabrieldsrod.gestao_ct.Model;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false)
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // RECEITA ou DESPESA

    @Column(name = "transaction_date", nullable = false)
    private LocalDate transactionDate;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
