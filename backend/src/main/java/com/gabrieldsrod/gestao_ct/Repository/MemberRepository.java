package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Model.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    long countByStatus(MemberStatus status);

    @Query("SELECT COUNT(m) FROM Member m WHERE m.registrationDate <= :endDate AND m.status NOT IN ('INACTIVE', 'EXPELLED', 'SUSPENDED')")
    long countActiveMembersUpTo(@Param("endDate") LocalDate endDate);

    Boolean existsByEmail(String email);

    Page<Member> findByStatus(MemberStatus status, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.status != 'INACTIVE' AND m.holder IS NULL AND m.dependents IS EMPTY")
    List<Member> findEligibleDependents();

    Page<Member> findTop10ByNameContainingIgnoreCase(String name, Pageable pageable);

    List<Member> findByStatusInAndHolderIsNull(List<MemberStatus> statusToBill);
}
