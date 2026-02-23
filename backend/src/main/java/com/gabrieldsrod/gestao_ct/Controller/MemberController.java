package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.MemberRegistrationDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberListingDTO;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("v1/api/members")
public class MemberController {

    private final MemberRepository memberRepo;

    private final PlanRepository planoRepo;

    public MemberController(MemberRepository memberRepo, PlanRepository planoRepo) {
        this.memberRepo = memberRepo;
        this.planoRepo = planoRepo;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerMember(@RequestBody MemberRegistrationDTO dados) {
        try {
            Plan plan = planoRepo.findById(dados.getPlanId()).orElseThrow(() -> new RuntimeException("Plano não " +
                    "encontrado com ID: " + dados.getPlanId()));

            Member newMember = new Member();
            newMember.setName(dados.getName());
            newMember.setEmail(dados.getEmail());
            newMember.setPreferredPaymentDay(dados.getPreferredPaymentDay());
            newMember.setWhatsapp(dados.getWhatsapp());
            newMember.setBirthDate(dados.getBirthDate());
            newMember.setPlan(plan);
            newMember.setActive(true);

            memberRepo.save(newMember);

            return ResponseEntity.ok().body(Map.of("mensagem", "Aluno cadastrado com sucesso", "alunoId",
                    newMember.getId()));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao cadastrar aluno: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<MemberListingDTO>> listMembers() {
        return ResponseEntity.ok(memberRepo.findAll().stream().map(MemberListingDTO::new).toList());
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<MemberListingDTO> getMemberById(@PathVariable Long id) {
        Member member =
                memberRepo.findById(id).orElseThrow(() -> new RuntimeException("Aluno não encontrado com ID: " + id));

        return ResponseEntity.ok(new MemberListingDTO(member));
    }

    @GetMapping("/search/active")
    public ResponseEntity<List<MemberListingDTO>> listActiveMembers() {
        List<MemberListingDTO> alunosAtivos = memberRepo.findByActiveTrue().stream().map(MemberListingDTO::new).toList();
        return ResponseEntity.ok(alunosAtivos);
    }

    @GetMapping("/search/{partialName}")
    public ResponseEntity<List<MemberListingDTO>> searchMembersByName(@PathVariable String partialName) {
        List<MemberListingDTO> alunos =
                memberRepo.findByNameContainingIgnoreCase(partialName).stream().map(MemberListingDTO::new).toList();
        return ResponseEntity.ok(alunos);
    }

}
