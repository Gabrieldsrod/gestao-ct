package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;
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

    public static PagamentoPendenteDTO fromEntity(AlunoPagamento pagamento){
        PagamentoPendenteDTO dto = new PagamentoPendenteDTO();
        dto.setPagamentoId(pagamento.getId());
        dto.setAlunoId(pagamento.getAluno().getId());
        dto.setNomeAluno(pagamento.getAluno().getNome());
        dto.setEmailAluno(pagamento.getAluno().getEmail());
        dto.setTelefoneAluno(pagamento.getAluno().getWhatsapp());
        dto.setNomePlano(pagamento.getAluno().getPlano().getNome());
        dto.setDiaPreferenciaPagamento(pagamento.getAluno().getDiaPreferenciaPagamento());
        dto.setDataVencimento(pagamento.getDataVencimento().format(DateUtils.BR_FORMATTER));
        dto.setValorCobrado(String.format("R$ %.2f", pagamento.getValorCobrado()));
        return dto;
    }
}
