package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Member;

public record MemberResponseDTO(
        Long id,
        String nome,
        String whatsapp,
        String email,
        String planName,
        String status,
        String registrationDate
) {
    public MemberResponseDTO(Member member) {
        this(member.getId(), member.getName(), member.getWhatsapp(), member.getEmail(), member.getPlan().getName(), member.getStatus().name(), member.getRegistrationDate().toString());
    }
}
