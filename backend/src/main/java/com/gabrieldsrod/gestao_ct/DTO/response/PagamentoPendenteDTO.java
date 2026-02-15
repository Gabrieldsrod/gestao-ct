package com.gabrieldsrod.gestao_ct.DTO.response;

import lombok.Data;

@Data
public class PagamentoPendenteDTO {

    private Long pagamentoId;
    private Long alunoId;
    private String nomeAluno;
    private String emailAluno;
    private String telefoneAluno;
    private String nomePlano;
    private Integer diaPreferenciaPagamento;
    private String dataVencimento; // Formato "dd/MM/yyyy"
    private String valorCobrado;   // Formato "R$ 100,00"
}
