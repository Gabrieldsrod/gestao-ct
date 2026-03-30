package com.gabrieldsrod.gestao_ct.Config;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Enums.TransactionType;
import com.gabrieldsrod.gestao_ct.Model.*;
import com.gabrieldsrod.gestao_ct.Repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final PlanRepository planRepo;
    private final CategoryRepository categoryRepo;
    private final FeeRepository feeRepo;
    private final MemberRepository memberRepo;
    private final MemberPaymentRepository paymentRepo;
    private final TransactionRepository transactionRepo;

    public DataSeeder(PlanRepository planRepo, CategoryRepository categoryRepo, FeeRepository feeRepo,
                      MemberRepository memberRepo, MemberPaymentRepository paymentRepo, TransactionRepository transactionRepo) {
        this.planRepo = planRepo;
        this.categoryRepo = categoryRepo;
        this.feeRepo = feeRepo;
        this.memberRepo = memberRepo;
        this.paymentRepo = paymentRepo;
        this.transactionRepo = transactionRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Se já existirem planos, consideramos que a base de dados já foi populada para evitar duplicações
        if (planRepo.count() > 0) {
            return;
        }

        // 1. POPULAR PLANOS
        Plan pIndividual = planRepo.save(new Plan("Individual", new BigDecimal("99.90")));
        Plan pIndCTStudio = planRepo.save(new Plan("Individual C.T + Studio", new BigDecimal("129.90")));
        Plan pCasal = planRepo.save(new Plan("Casal", new BigDecimal("179.90")));
        Plan pCasalCTStudio = planRepo.save(new Plan("Casal C.T + Studio", new BigDecimal("219.90")));

        // 2. POPULAR CATEGORIAS
        Category catMensalidade = categoryRepo.save(new Category("Mensalidades", TransactionType.INCOME));
        Category catProdutos = categoryRepo.save(new Category("Venda de Produtos", TransactionType.INCOME));
        Category catAluguel = categoryRepo.save(new Category("Aluguel", TransactionType.EXPENSE));
        Category catEquipamentos = categoryRepo.save(new Category("Equipamentos", TransactionType.EXPENSE));
        Category catManutencao = categoryRepo.save(new Category("Manutenção e Limpeza", TransactionType.EXPENSE));

        // 3. POPULAR TAXAS DAS MÁQUINAS (PAYMENT FEES)
        createFee(PaymentMethod.PIX, "0.00", "0.00", 0);
        createFee(PaymentMethod.CASH, "0.00", "0.00", 0);
        createFee(PaymentMethod.CREDIT_CARD, "3.99", "0.00", 30);
        createFee(PaymentMethod.DEBIT_CARD, "1.99", "0.00", 1);
        createFee(PaymentMethod.SLIP, "0.00", "3.50", 3); // Boleto geralmente tem taxa fixa

        // 4. POPULAR ALUNOS
        LocalDate hoje = LocalDate.now();
        LocalDate mesPassado = hoje.minusMonths(1);

        Member m1 = createMember("Gabriel Rodrigues", "gabriel@email.com", "11999999999", pIndividual, MemberStatus.ACTIVE, mesPassado);
        Member m2 = createMember("Carlos Eduardo", "carlos@email.com", "15991234567", pIndCTStudio, MemberStatus.ACTIVE, mesPassado);

        // Titular e Dependente (Plano Casal)
        Member titular = createMember("João Silva", "joao@email.com", "15988887777", pCasal, MemberStatus.ACTIVE, mesPassado);
        Member dependente = createDependent("Maria Silva", "maria@email.com", titular, pCasal);

        // Aluno Inadimplente
        Member mInadimplente = createMember("Pedro Souza", "pedro@email.com", "11977776666", pIndividual, MemberStatus.DELINQUENT, mesPassado.minusMonths(1));

        // 5. POPULAR HISTÓRICO FINANCEIRO E MENSALIDADES
        // Pagamentos do mês passado (Pagos via Cartão de Crédito e PIX para gerar taxas diferentes)
        registerHistoricalPayment(m1, mesPassado, PaymentMethod.PIX, catMensalidade);
        registerHistoricalPayment(m2, mesPassado, PaymentMethod.CREDIT_CARD, catMensalidade);
        registerHistoricalPayment(titular, mesPassado, PaymentMethod.DEBIT_CARD, catMensalidade);

        // Pagamento atrasado (Inadimplente)
        createPendingPayment(mInadimplente, mesPassado, PaymentStatus.OVERDUE);

        // Mensalidades do mês atual (Pendentes)
        createPendingPayment(m1, hoje, PaymentStatus.PENDING);
        createPendingPayment(m2, hoje, PaymentStatus.PENDING);
        createPendingPayment(titular, hoje, PaymentStatus.PENDING);
        createPendingPayment(mInadimplente, hoje, PaymentStatus.PENDING);

        // 6. POPULAR DESPESAS GERAIS PARA O FLUXO DE CAIXA
        registerExpense("Aluguel do Galpão", new BigDecimal("2500.00"), mesPassado.plusDays(5), catAluguel);
        registerExpense("Compra de Halteres", new BigDecimal("850.00"), mesPassado.plusDays(10), catEquipamentos);
        registerExpense("Conta de Luz", new BigDecimal("320.00"), hoje.minusDays(2), catManutencao);

        System.out.println("✅ Base de dados populada com sucesso (Planos, Categorias, Taxas, Alunos e Transações)!");
    }

    // --- MÉTODOS AUXILIARES PARA MANTER O CÓDIGO LIMPO ---

    private void createFee(PaymentMethod method, String percentage, String fixed, int days) {
        PaymentFee fee = new PaymentFee();
        fee.setPaymentMethod(method);
        fee.setPercentageFee(new BigDecimal(percentage));
        fee.setFixedFee(new BigDecimal(fixed));
        fee.setDaysToReceive(days);
        feeRepo.save(fee);
    }

    private Member createMember(String name, String email, String whatsapp, Plan plan, MemberStatus status, LocalDate registrationDate) {
        Member m = new Member();
        m.setName(name);
        m.setEmail(email);
        m.setWhatsapp(whatsapp);
        m.setBirthDate(LocalDate.of(1995, 5, 20));
        m.setPlan(plan);
        m.setStatus(status);
        m.setRegistrationDate(registrationDate);
        return memberRepo.save(m);
    }

    private Member createDependent(String name, String email, Member holder, Plan plan) {
        Member m = new Member();
        m.setName(name);
        m.setEmail(email);
        m.setWhatsapp(holder.getWhatsapp());
        m.setBirthDate(LocalDate.of(1998, 8, 15));
        m.setPlan(plan);
        m.setStatus(MemberStatus.ACTIVE);
        m.setRegistrationDate(holder.getRegistrationDate());
        m.setHolder(holder);
        return memberRepo.save(m);
    }

    private void createPendingPayment(Member member, LocalDate dueDate, PaymentStatus status) {
        MemberPayment payment = new MemberPayment();
        payment.setMember(member);
        payment.setDueDate(dueDate);
        payment.setAmountCharged(member.getPlan().getPrice());
        payment.setStatus(status);
        paymentRepo.save(payment);
    }

    private void registerHistoricalPayment(Member member, LocalDate dueDate, PaymentMethod method, Category category) {
        // 1. Cria a transação financeira
        BigDecimal grossAmount = member.getPlan().getPrice();
        PaymentFee feeConfig = feeRepo.findPaymentFeeByPaymentMethod(method).orElseThrow();

        BigDecimal feeAmount = grossAmount.multiply(feeConfig.getPercentageFee())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                .add(feeConfig.getFixedFee());

        Transaction income = new Transaction();
        income.setDescription("Mensalidade - " + member.getName());
        income.setGrossAmount(grossAmount);
        income.setFeeAmount(feeAmount);
        income.setNetAmount(grossAmount.subtract(feeAmount));
        income.setPaymentMethod(method);
        income.setType(TransactionType.INCOME);
        income.setTransactionDate(dueDate);
        income.setCategory(category);
        income = transactionRepo.save(income);

        // 2. Regista a mensalidade como paga e vincula a transação
        MemberPayment payment = new MemberPayment();
        payment.setMember(member);
        payment.setDueDate(dueDate);
        payment.setAmountCharged(grossAmount);
        payment.setStatus(PaymentStatus.PAID);
        payment.setPaymentDate(dueDate.atTime(10, 30));
        payment.setAmountPaid(grossAmount);
        payment.setTransaction(income);
        paymentRepo.save(payment);
    }

    private void registerExpense(String description, BigDecimal amount, LocalDate date, Category category) {
        Transaction expense = new Transaction();
        expense.setDescription(description);
        expense.setGrossAmount(amount);
        expense.setFeeAmount(BigDecimal.ZERO);
        expense.setNetAmount(amount);
        expense.setPaymentMethod(PaymentMethod.PIX);
        expense.setType(TransactionType.EXPENSE);
        expense.setTransactionDate(date);
        expense.setCategory(category);
        transactionRepo.save(expense);
    }
}