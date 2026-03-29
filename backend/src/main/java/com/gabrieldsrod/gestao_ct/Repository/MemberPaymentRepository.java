package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MemberPaymentRepository extends JpaRepository<MemberPayment, Long> {

    List<MemberPayment> findByMemberAndStatus(Member member, PaymentStatus status);

    Optional<MemberPayment> findTopByMemberOrderByDueDateDesc(Member member);

    @Query("SELECT p FROM MemberPayment p WHERE " +
            "(:status IS NULL OR p.status = :status) AND " +
            "(:memberId IS NULL OR p.member.id = :memberId) AND " +
            "(cast(:startDate as date) IS NULL OR p.dueDate >= :startDate) AND " +
            "(cast(:endDate as date) IS NULL OR p.dueDate <= :endDate)")
    Page<MemberPayment> searchWithFilters(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("memberId") Long memberId,
            @Param("status") PaymentStatus status,
            Pageable pageable
    );

    @Query("SELECT COUNT(p) > 0 FROM MemberPayment p WHERE " +
            "p.member = :member AND " +
            "EXTRACT(MONTH FROM p.dueDate) = :month AND " +
            "EXTRACT(YEAR FROM p.dueDate) = :year AND " +
            "p.status != 'CANCELLED'")
    boolean existsByMemberAndMonthAndYear(@Param("member") Member member, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT p FROM MemberPayment p WHERE " +
            "p.status = 'LATE' AND " +
            "p.dueDate < :limitDate AND " +
            "p.member.status = 'DELINQUENT'")
    List<MemberPayment> findPaymentsForInactivation(@Param("limitDate") LocalDate limitDate);

    @Query("SELECT p FROM MemberPayment p WHERE " +
            "p.status = 'PENDING' AND " +
            "p.dueDate < :today AND " +
            "p.member.status IN ('ACTIVE', 'DELINQUENT')")
    List<MemberPayment> findOverduePaymentsForActiveOrDelinquentMembers(@Param("today") LocalDate today);
}
