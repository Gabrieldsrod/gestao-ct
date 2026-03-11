package com.gabrieldsrod.gestao_ct.Scheduler;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
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

    @Transactional
    @Scheduled(cron = "0 0 1 * * *")
    public void generateMonthlyBills() {
        System.out.println("Gerando cobranças mensais para os membros...");

        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        List<MemberStatus> statusToBill = Arrays.asList(MemberStatus.ACTIVE, MemberStatus.DELINQUENT);
        List<Member> membersToBill = memberRepo.findByStatusInAndHolderIsNull(statusToBill);

        for (Member member : membersToBill) {
            boolean alreadyCharged = memberPaymentRepo.existsByMemberAndMonthAndYear(member, currentMonth, currentYear);

            if(!alreadyCharged) {
                Optional<MemberPayment> lastPayment = paymentService.findLastPaymentForMember(member);
                LocalDate nextDueDate;

                if (lastPayment.isPresent()) {
                    nextDueDate = lastPayment.get().getDueDate().plusMonths(1);
                } else {
                    nextDueDate = member.getRegistrationDate().withDayOfMonth(1).plusMonths(1);
                }

                LocalDate generateBillingDate = nextDueDate.minusDays(3);

                if (today.isEqual(generateBillingDate) || today.isAfter(generateBillingDate)) {
                    paymentService.generateCharge(member, nextDueDate);
                    System.out.println("Cobrança gerada com antecedência para: " + member.getName());
                }
            }
        }
    }
}
