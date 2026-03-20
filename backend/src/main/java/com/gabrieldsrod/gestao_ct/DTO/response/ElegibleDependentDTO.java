package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Member;

public record ElegibleDependentDTO(
        Long id,
        String name,
        String whatsapp
) {
    public ElegibleDependentDTO(Member member) {
        this(member.getId(), member.getName(), member.getWhatsapp());
    }
}
