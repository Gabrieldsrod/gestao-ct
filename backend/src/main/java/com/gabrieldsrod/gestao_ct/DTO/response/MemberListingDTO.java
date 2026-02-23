package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Member;

public record MemberListingDTO(
        Long id,
        String nome,
        String whatsapp,
        String email,
        String nomePlano,
        Boolean status
) {
    public MemberListingDTO(Member member) {
        this(member.getId(), member.getName(), member.getWhatsapp(), member.getEmail(), member.getPlan().getName(),
                member.getActive());
    }
}
