package com.gabrieldsrod.gestao_ct.DTO.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MemberRegistrationDTO {

    @NotBlank(message = "Nome não pode estar em branco")
    private String name;

    @NotBlank(message = "E-mail não pode estar em branco")
    @Email(message = "E-mail deve ser válido")
    private String email;

    @NotBlank(message = "WhatsApp não pode estar em branco")
    private String whatsapp;
    private LocalDate birthDate;
    private Long planId;
    private Long holderId; // Opcional, para dependentes

}
