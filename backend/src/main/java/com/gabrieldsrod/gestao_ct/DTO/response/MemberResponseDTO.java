package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;

public record MemberResponseDTO(
        Long id,
        String name,
        String birthDate,
        String whatsapp,
        String email,
        String planName,
        String status,
        String registrationDate
) {
    public MemberResponseDTO(Member member) {
        this(member.getId(), member.getName(), member.getBirthDate().format(DateUtils.BR_FORMATTER), member.getWhatsapp(), member.getEmail(), member.getPlan().getName(), member.getStatus().name(), member.getRegistrationDate().toString());
    }
}
