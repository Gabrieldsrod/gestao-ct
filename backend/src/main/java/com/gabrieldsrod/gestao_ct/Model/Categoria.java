package com.gabrieldsrod.gestao_ct.model;

import com.gabrieldsrod.gestao_ct.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "categorias")
@Data
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoTransacao tipo; // RECEITA ou DESPESA
}
