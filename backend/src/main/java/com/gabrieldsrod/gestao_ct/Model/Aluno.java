package com.gabrieldsrod.gestao_ct.Model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "alunos")
@Data
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20, nullable = false)
    private String whatsapp;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private Integer diaPreferenciaPagamento;
}
