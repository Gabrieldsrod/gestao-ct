package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import lombok.Data;

@Data
public class PaymentClearenceDTO {

    private PaymentMethod paymentMethod;
}
