package com.gabrieldsrod.gestao_ct.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "alunos_pagamentos")
@Data
public class AlunosPagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "aluno_id", nullable = false)
    private Aluno aluno;

    @Column(name = "data_vencimento", nullable = false)
    private Date dataVencimento;
    @Column(name = "valor_cobrado",nullable = false , precision = 19, scale = 2)
    private BigDecimal valorCobrado;

    // Campos para controle de pagamento, preenchidos quando o pagamento for realizado

    @Column(name = "data_pagamento")
    private Date dataPagamento;
    @Column(name = "valor_pago", precision = 19, scale = 2)
    private BigDecimal valorPago;

    @OneToOne
    @JoinColumn(name = "transacao_id", unique = true)
    private Transacao transacao;
}
