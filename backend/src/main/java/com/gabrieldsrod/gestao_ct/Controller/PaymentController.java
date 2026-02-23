package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.PaymentClearenceDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PendingPaymentDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Repository.MemberPaymentRepository;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/pagamentos")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    private final MemberPaymentRepository pagamentoRepo;

    private final MemberRepository alunoRepository;

    private final PlanRepository planoRepo;

    public PaymentController(PaymentService paymentService, MemberPaymentRepository alunoPagamentoRepo,
                             PlanRepository planoRepo, MemberRepository alunoRepository) {
        this.paymentService = paymentService;
        this.pagamentoRepo = alunoPagamentoRepo;
        this.planoRepo = planoRepo;
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/pending")
    public List<PendingPaymentDTO> listPendingPayments() {
        return pagamentoRepo.findByPaymentDateIsNull()
                .stream()
                .map(PendingPaymentDTO::fromEntity)
                .toList();
    }


    @PostMapping("/{id}/register")
    public ResponseEntity<PaymentReceiptDTO> registerPayment(@PathVariable Long id, @RequestBody PaymentClearenceDTO metodoPagamento) {
        MemberPayment pagamentoRegistrado = paymentService.registerPayment(id,
                    metodoPagamento.getPaymentMethod());
        PaymentReceiptDTO recibo = new PaymentReceiptDTO(
                    pagamentoRegistrado.getId(),
                    pagamentoRegistrado.getMember().getName(),
                    "PAGO");

        return ResponseEntity.ok(recibo);
    }

    @PostMapping("/generate-test")
    public ResponseEntity<?> gerarTeste() {
        try {
            Member member = new Member();
            member.setName("Joaozinho Almeida");
            member.setEmail("joaoalmeida@teste.com");
            member.setWhatsapp("15999999999");
            member.setBirthDate(LocalDate.of(2000, 1, 1));
            member.setPreferredPaymentDay(5);
            member.setActive(true);
            member.setPlan(planoRepo.findByName("Plano Básico")
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado")));

            alunoRepository.save(member);
            LocalDate proximaDataVencimento = LocalDate.now().plusDays(30);
            pagamentoRepo.save(paymentService.generateCharge(member, proximaDataVencimento));

            return ResponseEntity.ok().body(Map.of(
                    "mensagem", "Dados de teste gerados com sucesso",
                    "aluno", member.getName(),
                    "email", member.getEmail(),
                    "plano", member.getPlan().getName(),
                    "valorMensalidade", member.getPlan().getPrice()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Erro ao gerar dados de teste",
                    "detalhes", e.getMessage()
            ));
        }
    }
}

