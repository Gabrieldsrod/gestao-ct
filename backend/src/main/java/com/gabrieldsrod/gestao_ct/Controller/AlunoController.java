package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.AlunoCadastroDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.AlunoListagemDTO;
import com.gabrieldsrod.gestao_ct.Model.Aluno;
import com.gabrieldsrod.gestao_ct.Model.Plano;
import com.gabrieldsrod.gestao_ct.Repository.AlunoRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/api/alunos")
public class AlunoController {

    private final AlunoRepository alunoRepo;

    private final PlanoRepository planoRepo;

    public AlunoController(AlunoRepository alunoRepo, PlanoRepository planoRepo) {
        this.alunoRepo = alunoRepo;
        this.planoRepo = planoRepo;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<?> cadastrarAluno(@RequestBody AlunoCadastroDTO dados) {
        try {
            Plano plano = planoRepo.findById(dados.getPlanoId()).orElseThrow(() -> new RuntimeException("Plano não " +
                    "encontrado com ID: " + dados.getPlanoId()));

            Aluno novoAluno = new Aluno();
            novoAluno.setNome(dados.getNome());
            novoAluno.setEmail(dados.getEmail());
            novoAluno.setDiaPreferenciaPagamento(dados.getDiaPreferenciaPagamento());
            novoAluno.setWhatsapp(dados.getWhatsapp());
            novoAluno.setDataNascimento(dados.getDataNascimento());
            novoAluno.setPlano(plano);
            novoAluno.setAtivo(true);

            alunoRepo.save(novoAluno);

            return ResponseEntity.ok().body(Map.of("mensagem", "Aluno cadastrado com sucesso", "alunoId",
                    novoAluno.getId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<AlunoListagemDTO>> listarAlunos() {
        return ResponseEntity.ok(alunoRepo.findAll().stream().map(AlunoListagemDTO::new).toList());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<AlunoListagemDTO> obterAlunoPorId(@PathVariable Long id) {
        Aluno aluno =
                alunoRepo.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + id));

        return ResponseEntity.ok(new AlunoListagemDTO(aluno));
    }

    @GetMapping("/buscar/ativos")
    public ResponseEntity<List<AlunoListagemDTO>> listarAlunosAtivos() {
        List<AlunoListagemDTO> alunosAtivos = alunoRepo.findByAtivoTrue().stream().map(AlunoListagemDTO::new).toList();
        return ResponseEntity.ok(alunosAtivos);
    }

    @GetMapping("/buscar/{nomeParcial}")
    public ResponseEntity<List<AlunoListagemDTO>> buscarAlunosPorNome(@PathVariable String nomeParcial) {
        List<AlunoListagemDTO> alunos =
                alunoRepo.findByNomeContainingIgnoreCase(nomeParcial).stream().map(AlunoListagemDTO::new).toList();
        return ResponseEntity.ok(alunos);
    }

}
