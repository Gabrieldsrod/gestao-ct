package com.gabrieldsrod.gestao_ct.Scheduler;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class MemberStatusScheduler {

    private final MemberPaymentRepository paymentRepo;

    private final MemberRepository memberRepo;

    public MemberStatusScheduler(MemberPaymentRepository paymentRepo, MemberRepository memberRepo) {
        this.paymentRepo = paymentRepo;
        this.memberRepo = memberRepo;
    }

    @Transactional
    @Scheduled(cron = "0 0 2 * * *")
    public void checkAndMarkDelinquentMembers() {
        System.out.println("Iniciando verificação de inadimplência...");

        LocalDate today = LocalDate.now();
        List<MemberPayment> overduePayments = paymentRepo.findOverduePaymentsForActiveMembers(today);

        int count = 0;
        for (MemberPayment payment : overduePayments) {
            payment.setStatus(PaymentStatus.OVERDUE);
            Member holder = payment.getMember();

            if (holder.getStatus() != MemberStatus.DELINQUENT) {
                holder.setStatus(MemberStatus.DELINQUENT);

                if (holder.getDependents() != null && !holder.getDependents().isEmpty()) {
                    for (Member dependente : holder.getDependents()) {
                        dependente.setStatus(MemberStatus.DELINQUENT);
                    }
                }
                memberRepo.save(holder);
                count++;
            }
        }
        System.out.println("Verificação concluída. " + count + " alunos marcados como INADIMPLENTES.");
    }

    @Transactional
    @Scheduled(cron = "0 30 2 * * *")
    public void inactivateDelinquentMembers() {
        System.out.println("A iniciar a verificação para inativação de alunos...");

        // Define o limite de tempo (em dias) para considerar um aluno como inativo após se tornar inadimplente
        int toleranceDays = 10;
        LocalDate dataLimite = LocalDate.now().minusDays(toleranceDays);

        List<MemberPayment> oldPayments = paymentRepo.findPaymentsForInactivation(dataLimite);

        int count = 0;
        for (MemberPayment payment : oldPayments) {
            Member holder = payment.getMember();

            if (holder.getStatus() == MemberStatus.DELINQUENT) {
                holder.setStatus(MemberStatus.INACTIVE);

                // Inativa os dependentes, se existirem
                if (holder.getDependents() != null && !holder.getDependents().isEmpty()) {
                    for (Member dependente : holder.getDependents()) {
                        dependente.setStatus(MemberStatus.INACTIVE);
                    }
                }

                memberRepo.save(holder);
                count++;
            }
        }
        System.out.println("Limpeza concluída. " + count + " alunos marcados como INATIVOS (mais de " + toleranceDays + " dias de dívida).");
    }
}
