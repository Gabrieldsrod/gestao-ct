package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PendingPaymentDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

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

    public List<PendingPaymentDTO> listPending() {
        return paymentRepo.findByPaymentDateIsNull()
                .stream()
                .map(PendingPaymentDTO::fromEntity)
                .toList();
    }

    public Optional<MemberPayment> findLastPaymentForMember(Member member) {
        return paymentRepo.findTopByMemberOrderByDueDateDesc(member);
    }

    @Transactional
    public MemberPayment generateCharge(Member member, LocalDate dueDate) {
        if (member == null || (member.getStatus() != MemberStatus.ACTIVE && member.getStatus() != MemberStatus.PENDING) || member.getPlan() == null) {
            return null;
        }
        MemberPayment pagamento = new MemberPayment();
        pagamento.setMember(member);

        pagamento.setDueDate(dueDate);

        pagamento.setAmountCharged(member.getPlan().getPrice());
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

        if (payment.getPaymentDate() != null) {
            throw new BusinessRuleException("Pagamento já registrado");
        }

        Transaction income = transactionService.saveMembershipTransaction(payment, paymentMethod);

        payment.setPaymentDate(LocalDate.now());
        payment.setAmountPaid(payment.getAmountCharged());
        payment.setTransaction(income);

        Member member = payment.getMember();
        if (member.getStatus() == MemberStatus.DELINQUENT || member.getStatus() == MemberStatus.PENDING)
            member.setStatus(MemberStatus.ACTIVE);

        payment = paymentRepo.save(payment);

        return new PaymentReceiptDTO(
                payment.getId(),
                payment.getMember().getName(),
                "PAGO"
        );
    }
}
