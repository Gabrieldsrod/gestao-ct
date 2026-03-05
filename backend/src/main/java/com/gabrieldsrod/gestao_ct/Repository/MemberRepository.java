package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
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

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    List<Member> findByStatusAndHolderIsNull(MemberStatus memberStatus);

    Page<Member> findTop10ByNameContainingIgnoreCase(String name, Pageable pageable);
}
