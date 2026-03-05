package com.gabrieldsrod.gestao_ct.Repository;

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

    // LISTA DE INADIMPLENTES / A RECEBER
    // Traz tudo onde a Data de Pagamento está vazia (NULL)
    Page<MemberPayment> findByPaymentDateIsNull(Pageable pageable);

    // LISTA DE PAGOS
    // Traz tudo onde a Data de Pagamento NÃO está vazia
    Page<MemberPayment> findByPaymentDateIsNotNull(Pageable pageable);

    // Histórico financeiro de um aluno específico
    List<MemberPayment> findByMember(Member member);

    // Busca pagamentos que vencem em um mês específico (para gerar boletos)
    // Ex: Todos os vencimentos entre 01/02 e 28/02
    List<MemberPayment> findByPaymentDateBetween(LocalDate start, LocalDate end);

    Optional<MemberPayment> findTopByMemberOrderByDueDateDesc(Member member);

    @Query("SELECT p FROM MemberPayment p WHERE p.paymentDate IS NULL AND p.dueDate < :today AND p.member.status = 'ACTIVE'")
    List<MemberPayment> findOverduePaymentsForActiveMembers(@Param("today") LocalDate today);

    @Query("SELECT COUNT(p) > 0 FROM MemberPayment p WHERE p.member = :member AND EXTRACT(MONTH FROM p.dueDate) = :month AND EXTRACT(YEAR FROM p.dueDate) = :year")
    boolean existsByMemberAndMonthAndYear(@Param("member") Member member, @Param("month") Integer month, @Param("year") Integer year);

    @Query("SELECT p FROM MemberPayment p WHERE p.paymentDate IS NULL AND p.dueDate < :limitDate AND p.member.status = 'DELINQUENT'")
    List<MemberPayment> findPaymentsForInactivation(@Param("limitDate") LocalDate limitDate);

    List<MemberPayment> findByMemberAndPaymentDateIsNull(Member member);
}
