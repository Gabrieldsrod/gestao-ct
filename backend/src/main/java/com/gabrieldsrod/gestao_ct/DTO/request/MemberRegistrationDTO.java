package com.gabrieldsrod.gestao_ct.DTO.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRegistrationDTO {

    private String name;
    private String email;
    private String whatsapp;
    private LocalDate birthDate;
    private Long planId;
    private Long holderId; // Opcional, para dependentes

}
