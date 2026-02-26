package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PendingPaymentDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Model.Category;
import com.gabrieldsrod.gestao_ct.Model.Transaction;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.CategoryRepository;
import com.gabrieldsrod.gestao_ct.Repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PaymentService {

    private final MemberPaymentRepository paymentRepo;
    private final TransactionRepository transactionRepo;
    private final CategoryRepository categoryRepo;

    public PaymentService(MemberPaymentRepository paymentRepo, TransactionRepository transactionRepo, CategoryRepository categoriaRepo) {
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
        this.categoryRepo = categoriaRepo;
    }

    public List<PendingPaymentDTO> listPending() {
        return paymentRepo.findByPaymentDateIsNull()
                .stream()
                .map(PendingPaymentDTO::fromEntity)
                .toList();
    }

    @Transactional
    public MemberPayment generateCharge(Member member, LocalDate dueDate) {
        if (member == null || !member.getActive() || member.getPlan() == null) {
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
    public PaymentReceiptDTO register(Long paymentId, PaymentMethod paymentMethod) {
        MemberPayment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado"));

        if (payment.getPaymentDate() != null) {
            throw new BusinessRuleException("Pagamento já registrado");
        }

        Transaction income = new Transaction();
        income.setDescription("Mensalidade - " + payment.getMember().getName());
        income.setAmount(payment.getAmountCharged());
        income.setPaymentMethod(paymentMethod);
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(LocalDate.now());

        Category category = categoryRepo.findByName("Mensalidade")
                .orElseThrow(() -> new ResourceNotFoundException("Categoria 'Mensalidade' não encontrada"));
        income.setCategory(category);

        transactionRepo.save(income);

        payment.setPaymentDate(LocalDate.now());
        payment.setAmountPaid(payment.getAmountCharged());
        payment.setTransaction(income);

        payment = paymentRepo.save(payment);

        return new PaymentReceiptDTO(
                payment.getId(),
                payment.getMember().getName(),
                "PAGO"
        );
    }
}
