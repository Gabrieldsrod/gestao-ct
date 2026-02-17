package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.BaixaPagamentoDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PagamentoPendenteDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.ReciboPagamentoDTO;
import com.gabrieldsrod.gestao_ct.Model.Aluno;
import com.gabrieldsrod.gestao_ct.Model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.Repository.AlunoPagamentoRepository;
import com.gabrieldsrod.gestao_ct.Repository.AlunoRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanoRepository;
import com.gabrieldsrod.gestao_ct.Service.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/pagamentos")
@CrossOrigin(origins = "*")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    private final AlunoPagamentoRepository pagamentoRepo;

    private final AlunoRepository alunoRepository;

    private final PlanoRepository planoRepo;

    public PagamentoController(PagamentoService pagamentoService, AlunoPagamentoRepository alunoPagamentoRepo,
                               PlanoRepository planoRepo, AlunoRepository alunoRepository) {
        this.pagamentoService = pagamentoService;
        this.pagamentoRepo = alunoPagamentoRepo;
        this.planoRepo = planoRepo;
        this.alunoRepository = alunoRepository;
    }

    @GetMapping("/teste")
    public ResponseEntity<?> teste() {
        return ResponseEntity.ok().body(Map.of(
                "mensagem", "Endpoint de pagamentos funcionando corretamente",
                "status", "OK",
                "timestamp", LocalDate.now()
        ));
    }

    @GetMapping("/pendentes")
    public List<PagamentoPendenteDTO> listarPagamentosPendentes() {
        return pagamentoRepo.findByDataPagamentoIsNull()
                .stream()
                .map(PagamentoPendenteDTO::fromEntity)
                .toList();
    }


    @PostMapping("/{id}/registrar")
    public ResponseEntity<?> registrarPagamento(@PathVariable Long id, @RequestBody BaixaPagamentoDTO metodoPagamento) {
        AlunoPagamento pagamentoRegistrado = pagamentoService.registrarPagamento(id,
                    metodoPagamento.getMetodoPagamento());
        ReciboPagamentoDTO recibo = new ReciboPagamentoDTO(
                    pagamentoRegistrado.getId(),
                    pagamentoRegistrado.getAluno().getNome(),
                    "PAGO");

        return ResponseEntity.ok(recibo);
    }

    @PostMapping("/gerar-teste")
    public ResponseEntity<?> gerarTeste() {
        try {
            Aluno aluno = new Aluno();
            aluno.setNome("Joaozinho Almeida");
            aluno.setEmail("joaoalmeida@teste.com");
            aluno.setWhatsapp("15999999999");
            aluno.setDataNascimento(LocalDate.of(2000, 1, 1));
            aluno.setDiaPreferenciaPagamento(5);
            aluno.setAtivo(true);
            aluno.setPlano(planoRepo.findByNome("Plano Básico")
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado")));

            alunoRepository.save(aluno);
            LocalDate proximaDataVencimento = LocalDate.now().plusDays(30);
            pagamentoRepo.save(pagamentoService.gerarCobranca(aluno, proximaDataVencimento));

            return ResponseEntity.ok().body(Map.of(
                    "mensagem", "Dados de teste gerados com sucesso",
                    "aluno", aluno.getNome(),
                    "email", aluno.getEmail(),
                    "plano", aluno.getPlano().getNome(),
                    "valorMensalidade", aluno.getPlano().getValorMensalidade()
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "erro", "Erro ao gerar dados de teste",
                    "detalhes", e.getMessage()
            ));
        }
    }
}

