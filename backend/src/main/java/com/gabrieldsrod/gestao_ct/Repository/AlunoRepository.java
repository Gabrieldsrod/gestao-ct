package com.gabrieldsrod.gestao_ct.repository;

import com.gabrieldsrod.gestao_ct.model.Aluno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlunoRepository extends JpaRepository<Aluno, Long> {

    Aluno findByEmail(String email);

    Aluno findByWhatsapp(String whatsapp);

    // Para buscar alunos pelo nome (parcial, ignore case)
    List<Aluno> findByNomeContainingIgnoreCase(String nome);

    // Para listar quem prefere pagar no dia X
    List<Aluno> findByDiaPreferenciaPagamento(Integer dia);
}
