package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.MemberRegistrationDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberUpdateResponseDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberResponseDTO;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemberService {

    private final MemberRepository memberRepo;

    private final PlanRepository planRepo;

    public MemberService(MemberRepository memberRepo, PlanRepository planoRepo) {
        this.memberRepo = memberRepo;
        this.planRepo = planoRepo;
    }

    @Transactional
    public MemberResponseDTO register(MemberRegistrationDTO data) {
        Plan plan = planRepo.findById(data.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + data.getPlanId()));

        if(memberRepo.existsByEmail(data.getEmail())) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com o email: " + data.getEmail());
        }

        Member newMember = new Member();
        newMember.setName(data.getName());
        newMember.setEmail(data.getEmail());
        newMember.setPreferredPaymentDay(data.getPreferredPaymentDay());
        newMember.setWhatsapp(data.getWhatsapp());
        newMember.setBirthDate(data.getBirthDate());
        newMember.setPlan(plan);
        newMember.setActive(true);

        if (data.getHolderId() != null) {
            Member holder = memberRepo.findById(data.getHolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno titular não encontrado com ID: " + data.getHolderId()));
            newMember.setHolder(holder);
        }

        newMember = memberRepo.save(newMember);

        return new MemberResponseDTO(newMember);
    }

    @Transactional
    public Page<MemberResponseDTO> pageMembers(Pageable pageable) {
        return memberRepo.findAll(pageable).map(MemberResponseDTO::new);
    }

    @Transactional
    public Page<MemberResponseDTO> pageActiveMembers(Pageable pageable) {
        return memberRepo.findByActiveTrue(pageable).map(MemberResponseDTO::new);
    }

    @Transactional
    public MemberResponseDTO getById(Long id) {
        Member member = this.getMemberById(id);
        return new MemberResponseDTO(member);
    }

    @Transactional
    public List<MemberResponseDTO> searchByPartialName(String partialName) {
        return memberRepo.findByNameContainingIgnoreCase(partialName).stream().map(MemberResponseDTO::new).toList();
    }

    @Transactional
    public MemberUpdateResponseDTO updateMember(Long id, MemberRegistrationDTO data) {
        Member member = this.getMemberById(id);

        Plan plan = planRepo.findById(data.getPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado com ID: " + data.getPlanId()));

        member.setName(data.getName());
        member.setEmail(data.getEmail());
        member.setPreferredPaymentDay(data.getPreferredPaymentDay());
        member.setWhatsapp(data.getWhatsapp());
        member.setBirthDate(data.getBirthDate());
        member.setPlan(plan);

        member = memberRepo.save(member);

        String message = "Aluno atualizado com sucesso.";
        return new MemberUpdateResponseDTO(message, member.getId());
    }

    @Transactional
    public MemberUpdateResponseDTO inactivate(Long id) {
        Member member = this.getMemberById(id);
        member.setActive(false);
        member = memberRepo.save(member);

        String message = "Aluno inativado com sucesso. Ele não receberá mais cobranças, mas seus dados permanecerão no sistema.";
        return new MemberUpdateResponseDTO(message, member.getId());
    }

    @Transactional
    public MemberUpdateResponseDTO activate(Long id) {
        Member member = this.getMemberById(id);
        member.setActive(true);
        member = memberRepo.save(member);

        String message = "Aluno ativado com sucesso. Ele receberá cobranças normalmente a partir de agora.";

        return new MemberUpdateResponseDTO(message, member.getId());
    }

    private Member getMemberById(Long id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));
    }
}
