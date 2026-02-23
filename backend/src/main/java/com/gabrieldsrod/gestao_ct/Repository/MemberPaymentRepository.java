package com.gabrieldsrod.gestao_ct.Repository;

import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MemberPaymentRepository extends JpaRepository<MemberPayment, Long> {

    // LISTA DE INADIMPLENTES / A RECEBER
    // Traz tudo onde a Data de Pagamento está vazia (NULL)
    List<MemberPayment> findByPaymentDateIsNull();

    // LISTA DE PAGOS
    // Traz tudo onde a Data de Pagamento NÃO está vazia
    List<MemberPayment> findByPaymentDateIsNotNull();

    // Histórico financeiro de um aluno específico
    List<MemberPayment> findByMember(Member member);

    // Busca pagamentos que vencem em um mês específico (para gerar boletos)
    // Ex: Todos os vencimentos entre 01/02 e 28/02
    List<MemberPayment> findByPaymentDateBetween(LocalDate start, LocalDate end);
}
