package com.gabrieldsrod.gestao_ct.Scheduler;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component
public class BillingScheduler {

    private final MemberRepository memberRepo;

    private final MemberPaymentRepository memberPaymentRepo;

    private final PaymentService paymentService;

    public BillingScheduler(MemberRepository memberRepo, MemberPaymentRepository memberPaymentRepo, PaymentService paymentService) {
        this.memberRepo = memberRepo;
        this.memberPaymentRepo = memberPaymentRepo;
        this.paymentService = paymentService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void generateMonthlyBills() {
        System.out.println("Gerando cobranças mensais para os membros...");

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        List<Member> activeMembers = memberRepo.findByStatusAndHolderIsNull(MemberStatus.ACTIVE);

        for (Member member : activeMembers) {
            boolean alreadyCharged = memberPaymentRepo.existsByMemberAndMonthAndYear(member, currentMonth, currentYear);

            if(!alreadyCharged) {
                Optional<MemberPayment> lastPayment = paymentService.findLastPaymentForMember(member);
                LocalDate nextDueDate;

                if (lastPayment.isPresent()) {
                    nextDueDate = lastPayment.get().getDueDate().plusMonths(1);
                } else {
                    nextDueDate = member.getRegistrationDate().withDayOfMonth(1).plusMonths(1);
                }

                paymentService.generateCharge(member, nextDueDate);
                System.out.println("Cobrança gerada para: " + member.getName());
            }
        }
    }
}
