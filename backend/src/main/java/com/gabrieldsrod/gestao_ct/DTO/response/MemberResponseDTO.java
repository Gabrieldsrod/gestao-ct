package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;

public record MemberResponseDTO(
        Long id,
        String name,
        String birthDate,
        String whatsapp,
        String email,
        PlanResponseDTO plan,
        String status,
        String registrationDate,
        String holderName,
        String createdAt,
        String updatedAt
) {
    public MemberResponseDTO(Member member) {
        this(member.getId(), member.getName(), member.getBirthDate().format(DateUtils.BR_FORMATTER_DATE), member.getWhatsapp(), member.getEmail(), member.getPlan() != null ? new PlanResponseDTO(member.getPlan()) : null , member.getStatus().name(), member.getRegistrationDate().toString(), member.getHolder() != null ? member.getHolder().getName() : null, member.getCreatedAt().toString(), member.getUpdatedAt().toString());
    }
}
