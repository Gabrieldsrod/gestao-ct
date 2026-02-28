package com.gabrieldsrod.gestao_ct.DTO.request;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRegistrationDTO {

    private String name;
    private String email;
    private String whatsapp;
    private LocalDate birthDate;
    private Long planId;
    private Integer preferredPaymentDay;
    private Long holderId; // Opcional, para dependentes

    private PaymentMethod paymentMethod;

}
