package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
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

@Service
public class PaymentService {

    private final MemberPaymentRepository pagamentoRepo;
    private final TransactionRepository transacaoRepo;
    private final CategoryRepository categoriaRepo;

    public PaymentService(MemberPaymentRepository pagamentoRepo, TransactionRepository transacaoRepo, CategoryRepository categoriaRepo) {
        this.pagamentoRepo = pagamentoRepo;
        this.transacaoRepo = transacaoRepo;
        this.categoriaRepo = categoriaRepo;
    }

    public MemberPayment generateCharge(Member member, LocalDate dataVencimento) {
        if (!member.getActive() || member.getPlan() == null) {
            return null; // Ignora se o aluno está inativo ou sem plano
        }

        MemberPayment pagamento = new MemberPayment();
        pagamento.setMember(member);

        pagamento.setDueDate(dataVencimento);

        pagamento.setAmountCharged(member.getPlan().getPrice());
        pagamento.setPaymentDate(null);
        pagamento.setAmountPaid(null);
        pagamento.setTransaction(null);

        return pagamentoRepo.save(pagamento);
    }

    @Transactional
    public MemberPayment registerPayment(Long pagamentoId, PaymentMethod paymentMethod) {
        MemberPayment pagamento = pagamentoRepo.findById(pagamentoId)
                .orElseThrow(() -> new RuntimeException("Pagamento não encontrado"));

        if (pagamento.getPaymentDate() != null) {
            throw new RuntimeException("Pagamento já registrado");
        }

        Transaction entrada = new Transaction();
        entrada.setDescription("Mensalidade - " + pagamento.getMember().getName());
        entrada.setAmount(pagamento.getAmountCharged());
        entrada.setPaymentMethod(paymentMethod);
        entrada.setType(TransactionType.INCOME);
        entrada.setTransactionDate(LocalDate.now());

        Category category = categoriaRepo.findByName("Mensalidade")
                .orElseThrow(() -> new RuntimeException("Categoria 'Mensalidade' não encontrada"));
        entrada.setCategory(category);

        transacaoRepo.save(entrada);

        pagamento.setPaymentDate(LocalDate.now());
        pagamento.setAmountPaid(pagamento.getAmountCharged());
        pagamento.setTransaction(entrada);

        generateCharge(pagamento.getMember(), pagamento.getDueDate());

        return pagamentoRepo.save(pagamento);
    }
}
