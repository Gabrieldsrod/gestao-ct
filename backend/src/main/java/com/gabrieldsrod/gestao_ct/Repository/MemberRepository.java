package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email);

    Boolean existsByEmail(String email);

    Member findByWhatsapp(String whatsapp);

    // Para buscar alunos pelo nome (parcial, ignore case)
    List<Member> findByNameContainingIgnoreCase(String name);

    // Para listar apenas os alunos ativos
    List<Member> findByActiveTrue();

    Page<Member> findByActiveTrue(Pageable pageable);

    Page<Member> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
