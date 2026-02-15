package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.MetodoPagamento;
import lombok.Data;

@Data
public class BaixaPagamentoDTO {

    private MetodoPagamento metodoPagamento;
}
