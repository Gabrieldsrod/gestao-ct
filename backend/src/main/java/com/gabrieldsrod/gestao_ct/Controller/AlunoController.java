package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.AlunoCadastroDTO;
import com.gabrieldsrod.gestao_ct.Model.Aluno;
import com.gabrieldsrod.gestao_ct.Model.Plano;
import com.gabrieldsrod.gestao_ct.Repository.AlunoRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
            Plano plano = planoRepo.findById(dados.getPlanoId())
                    .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + dados.getPlanoId()));

            Aluno novoAluno = new Aluno();
            novoAluno.setNome(dados.getNome());
            novoAluno.setEmail(dados.getEmail());
            novoAluno.setDiaPreferenciaPagamento(dados.getDiaPreferenciaPagamento());
            novoAluno.setWhatsapp(dados.getWhatsapp());
            novoAluno.setDataNascimento(dados.getDataNascimento());
            novoAluno.setPlano(plano);
            novoAluno.setAtivo(true);

            alunoRepo.save(novoAluno);

            return ResponseEntity.ok().body(Map.of(
                    "mensagem", "Aluno cadastrado com sucesso",
                    "alunoId", novoAluno.getId())
            );

        }
            catch (Exception e) {
                return ResponseEntity.badRequest().body("Erro ao cadastrar aluno: " + e.getMessage());
            }
    }

}
