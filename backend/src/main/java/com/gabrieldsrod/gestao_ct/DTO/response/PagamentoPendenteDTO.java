package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;

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
    private BigDecimal valorCobrado;

    public static PagamentoPendenteDTO fromEntity(AlunoPagamento pagamento){
        PagamentoPendenteDTO dto = new PagamentoPendenteDTO();
        dto.setPagamentoId(pagamento.getId());
        dto.setAlunoId(pagamento.getAluno().getId());
        dto.setNomeAluno(pagamento.getAluno().getNome());
        dto.setEmailAluno(pagamento.getAluno().getEmail());
        dto.setTelefoneAluno(pagamento.getAluno().getWhatsapp());
        dto.setNomePlano(pagamento.getAluno().getPlano().getNome());
        dto.setDiaPreferenciaPagamento(pagamento.getAluno().getDiaPreferenciaPagamento());
        dto.setDataVencimento(pagamento.getDataVencimento().format(DateUtils.BR_FORMATTER));    // Formata a data para "dd/MM/yyyy"
        dto.setValorCobrado(pagamento.getValorCobrado());
        return dto;
    }
}
