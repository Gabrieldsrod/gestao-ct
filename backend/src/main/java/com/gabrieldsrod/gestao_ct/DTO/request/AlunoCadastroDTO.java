package com.gabrieldsrod.gestao_ct.DTO.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AlunoCadastroDTO {

    private String nome;
    private String email;
    private String whatsapp;
    private LocalDate dataNascimento;
    private Long planoId;
    private Integer diaPreferenciaPagamento;

}
