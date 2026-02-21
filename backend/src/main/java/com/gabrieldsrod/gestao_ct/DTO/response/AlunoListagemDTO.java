package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.Aluno;

public record AlunoListagemDTO(
        Long id,
        String nome,
        String whatsapp,
        String email,
        String nomePlano,
        Boolean status
) {
    public AlunoListagemDTO(Aluno aluno) {
        this(aluno.getId(), aluno.getNome(), aluno.getWhatsapp(), aluno.getEmail(), aluno.getPlano().getNome(), aluno.getAtivo());
    }
}
