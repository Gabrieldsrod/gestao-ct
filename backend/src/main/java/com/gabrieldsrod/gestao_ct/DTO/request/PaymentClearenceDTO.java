package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;

public record PaymentClearenceDTO (
        PaymentMethod paymentMethod
) {

}
