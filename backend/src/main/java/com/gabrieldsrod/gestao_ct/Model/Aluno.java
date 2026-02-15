package com.gabrieldsrod.gestao_ct.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "alunos")
@Data
@EqualsAndHashCode(callSuper = true)
public class Aluno extends DadosPessoais{

    @Column(name = "dia_preferencia_pagamento", nullable = false)
    private Integer diaPreferenciaPagamento;

    @Column(nullable = false)
    private Boolean ativo = true;

    @JoinColumn(name = "plano_id", nullable = false)
    @ManyToOne
    private Plano plano;
}
