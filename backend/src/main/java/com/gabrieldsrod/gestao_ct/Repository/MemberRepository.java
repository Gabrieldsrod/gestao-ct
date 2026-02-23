package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Member findByWhatsapp(String whatsapp);

    // Para buscar alunos pelo nome (parcial, ignore case)
    List<Member> findByNameContainingIgnoreCase(String name);

    // Para listar apenas os alunos ativos
    List<Member> findByActiveTrue();

    // Para listar quem prefere pagar no dia X
    List<Member> findByPreferencePaymentDate(Integer day);
}
