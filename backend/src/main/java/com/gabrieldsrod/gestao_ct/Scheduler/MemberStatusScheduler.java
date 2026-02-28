package com.gabrieldsrod.gestao_ct.Scheduler;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
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
    @Scheduled(cron = "0 0 2 * * *") // Roda às 2h da manhã todos dias
    public void checkAndMarkDelinquentMembers() {
        System.out.println("Iniciando verificação de inadimplência...");

        LocalDate today = LocalDate.now();
        List<MemberPayment> overduePayments = paymentRepo.findOverduePaymentsForActiveMembers(today);

        int count = 0;
        for (MemberPayment payment : overduePayments) {
            Member holder = payment.getMember();
            holder.setStatus(MemberStatus.DELINQUENT);
            if (holder.getDependents() != null && !holder.getDependents().isEmpty()) {
                for (Member dependente : holder.getDependents()) {
                    dependente.setStatus(MemberStatus.DELINQUENT);
                }
                memberRepo.save(holder);
                count++;
            }
            count++;
        }
        System.out.println("Verificação concluída. " + count + " alunos marcados como INADIMPLENTES.");
    }
}
