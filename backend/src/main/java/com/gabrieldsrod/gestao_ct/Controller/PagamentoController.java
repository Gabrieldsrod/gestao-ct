package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.BaixaPagamentoDTO;

import com.gabrieldsrod.gestao_ct.DTO.response.PagamentoPendenteDTO;
import com.gabrieldsrod.gestao_ct.model.Aluno;
import com.gabrieldsrod.gestao_ct.model.AlunoPagamento;
import com.gabrieldsrod.gestao_ct.repository.AlunoPagamentoRepository;
import com.gabrieldsrod.gestao_ct.repository.AlunoRepository;
import com.gabrieldsrod.gestao_ct.repository.PlanoRepository;
import com.gabrieldsrod.gestao_ct.service.PagamentoService;
import com.gabrieldsrod.gestao_ct.utils.DateUtils;
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
                "mensagem", "Endpoint de pagamentos funcionando corretamente, como deveria ser",
                "status", "OK",
                "timestamp", LocalDate.now()
        ));
    }

    @GetMapping("/pendentes")
    public List<PagamentoPendenteDTO> listarPagamentosPendentes() {
        List<AlunoPagamento> pagamentosPendentes = pagamentoRepo.findByDataPagamentoIsNull();
        return pagamentosPendentes.stream().map(p -> {
            PagamentoPendenteDTO dto = new PagamentoPendenteDTO();
            dto.setPagamentoId(p.getId());
            dto.setAlunoId(p.getAluno().getId());
            dto.setNomeAluno(p.getAluno().getNome());
            dto.setEmailAluno(p.getAluno().getEmail());
            dto.setTelefoneAluno(p.getAluno().getWhatsapp());
            dto.setNomePlano(p.getAluno().getPlano().getNome());
            dto.setDiaPreferenciaPagamento(p.getAluno().getDiaPreferenciaPagamento());
            dto.setDataVencimento(p.getDataVencimento().format(DateUtils.BR_FORMATTER));
            dto.setValorCobrado(String.format("R$ %.2f", p.getValorCobrado()));
            return dto;
        }).toList();
    }


    @PostMapping("/{id}/registrar")
    public ResponseEntity<?> registrarPagamento(@PathVariable Long id, @RequestBody BaixaPagamentoDTO metodoPagamento) {
        try {
            AlunoPagamento pagamentoRegistrado = pagamentoService.registrarPagamento(id,
                    metodoPagamento.getMetodoPagamento());
            return ResponseEntity.ok(pagamentoRegistrado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
            aluno.setPlano(planoRepo.findByNome("Plano 1")
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado")));

            alunoRepository.save(aluno);
            pagamentoRepo.save(pagamentoService.gerarCobranca(aluno, LocalDate.now().plusDays(30)));

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

