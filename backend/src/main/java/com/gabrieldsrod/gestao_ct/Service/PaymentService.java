package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final MemberPaymentRepository paymentRepo;
    private final TransactionService transactionService;

    public PaymentService(MemberPaymentRepository paymentRepo, TransactionService transactionService) {
        this.paymentRepo = paymentRepo;
        this.transactionService = transactionService;
    }

    public Page<PaymentResponseDTO> getAllPayments(PaymentStatus status, Pageable pageable) {
        Page<MemberPayment> paymentsPage;

        if (status != null) {
            paymentsPage = paymentRepo.findByStatus(status, pageable);
        } else {
            paymentsPage = paymentRepo.findAll(pageable);
        }

        return paymentsPage.map(PaymentResponseDTO::new);
    }

    public Optional<MemberPayment> findLastPaymentForMember(Member member) {
        return paymentRepo.findTopByMemberOrderByDueDateDesc(member);
    }

    public List<MemberPayment> findByMemberAndPaymentDateIsNull(Member member) {
        return paymentRepo.findByMemberAndPaymentDateIsNull(member);
    }

    @Transactional
    public void updatePendingChargesForPlanChange(Member member, BigDecimal newPrice) {

        List<MemberPayment> pendingPayments = paymentRepo.findByMemberAndStatus(member, PaymentStatus.PENDING);

        for (MemberPayment payment : pendingPayments) {
            payment.setAmountCharged(newPrice);
            paymentRepo.save(payment);
        }
    }

    @Transactional
    public MemberPayment generateCharge(Member member, LocalDate dueDate) {
        if (member == null || member.getPlan() == null || (member.getStatus() != MemberStatus.ACTIVE && member.getStatus() != MemberStatus.PENDING && member.getStatus() != MemberStatus.DELINQUENT)) {
            return null;
        }

        int referenceMonth = dueDate.getMonthValue();
        int referenceYear = dueDate.getYear();

        boolean alreadyCharged = paymentRepo.existsByMemberAndMonthAndYear(member, referenceMonth, referenceYear);

        if (alreadyCharged) {
            return null;
        }

        MemberPayment pagamento = new MemberPayment();
        pagamento.setMember(member);
        pagamento.setDueDate(dueDate);
        pagamento.setAmountCharged(member.getPlan().getPrice());
        pagamento.setStatus(PaymentStatus.PENDING);
        pagamento.setPaymentDate(null);
        pagamento.setAmountPaid(null);
        pagamento.setTransaction(null);

        paymentRepo.save(pagamento);
        return pagamento;
    }

    @Transactional
    public PaymentReceiptDTO registerPayment(Long paymentId, PaymentMethod paymentMethod) {
        MemberPayment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        if (payment.getStatus() == PaymentStatus.PAID) {
            throw new BusinessRuleException("Pagamento já registrado");
        }
        if (payment.getStatus() == PaymentStatus.CANCELED) {
            throw new BusinessRuleException("Não é possível pagar uma cobrança cancelada");
        }

        Transaction income = transactionService.saveMembershipTransaction(payment, paymentMethod);

        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(LocalDate.now());
        payment.setAmountPaid(payment.getAmountCharged());
        payment.setTransaction(income);

        Member member = payment.getMember();
        if (member.getStatus() == MemberStatus.DELINQUENT || member.getStatus() == MemberStatus.PENDING) {
            member.setStatus(MemberStatus.ACTIVE);
        }

        if (member.getDependents() != null && !member.getDependents().isEmpty()) {
            for (Member dependent : member.getDependents()) {
                if (dependent.getStatus() == MemberStatus.DELINQUENT || dependent.getStatus() == MemberStatus.PENDING) {
                    dependent.setStatus(MemberStatus.ACTIVE);
                }
            }
        }

        payment = paymentRepo.save(payment);

        return new PaymentReceiptDTO(
                payment.getId(),
                payment.getMember().getName(),
                payment.getStatus().name()
        );
    }

    public void cancelPendingCharges(Member member) {
        List<MemberPayment> pendingPayments = paymentRepo.findByMemberAndStatus(member, PaymentStatus.PENDING);

        for (MemberPayment payment : pendingPayments) {
                payment.setStatus(PaymentStatus.CANCELED);
                paymentRepo.save(payment);
        }
    }

    public Boolean existsByMemberAndMonthAndYear(Member member, int currentMonth, int currentYear) {
        return paymentRepo.existsByMemberAndMonthAndYear(member, currentMonth, currentYear);
    }
}