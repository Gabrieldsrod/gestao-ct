package com.gabrieldsrod.gestao_ct.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "alunos_pagamentos")
@Data
public class AlunoPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;       // Data de vencimento da mensalidade
    @Column(name = "valor_cobrado",nullable = false , precision = 19, scale = 2)
    private BigDecimal valorCobrado;

    // Campos para controle de pagamento, preenchidos quando o pagamento for realizado
    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;
    @Column(name = "valor_pago", precision = 19, scale = 2)
    private BigDecimal valorPago;

    @OneToOne
    @JoinColumn(name = "transacao_id", unique = true)
    private Transacao transacao;
}
