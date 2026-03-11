package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.MemberRegistrationDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberUpdateResponseDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.MemberResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.BusinessRuleException;
import com.gabrieldsrod.gestao_ct.Infra.Exceptions.ResourceNotFoundException;
import com.gabrieldsrod.gestao_ct.Model.Member;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class MemberService {

    private final MemberRepository memberRepo;

    private final PlanService planService;
    private final PaymentService paymentService;

    public MemberService(MemberRepository memberRepo, PlanService planService, PaymentService paymentService) {
        this.memberRepo = memberRepo;
        this.planService = planService;
        this.paymentService = paymentService;
    }

    public long countByStatus(MemberStatus status) {
        return memberRepo.countByStatus(status);
    }

    public long countActiveMembersUpTo(LocalDate endDate) {
        return memberRepo.countActiveMembersUpTo(endDate);
    }

    public Page<MemberResponseDTO> getAllMembers(MemberStatus status, Pageable pageable) {
        Page<Member> membersPage;

        if (status != null) {
            membersPage = memberRepo.findByStatus(status, pageable);
        } else {
            membersPage = memberRepo.findAll(pageable);
        }

        return membersPage.map(MemberResponseDTO::new);
    }

    @Transactional
    public Page<MemberResponseDTO> pageActiveMembers(Pageable pageable) {
        return memberRepo.findByStatus(MemberStatus.ACTIVE, pageable).map(MemberResponseDTO::new);
    }

    @Transactional
    public MemberResponseDTO getById(Long id) {
        Member member = this.getMemberById(id);
        return new MemberResponseDTO(member);
    }

    @Transactional
    public Page<MemberResponseDTO> searchByPartialName(String partialName, Pageable pageable) {
        return memberRepo.findTop10ByNameContainingIgnoreCase(partialName, pageable).map(MemberResponseDTO::new);
    }

    @Transactional
    public MemberResponseDTO register(MemberRegistrationDTO data) {
        Plan plan = planService.getById(data.getPlanId());

        if(memberRepo.existsByEmail(data.getEmail())) {
            throw new IllegalArgumentException("Já existe um aluno cadastrado com o email: " + data.getEmail());
        }

        Member newMember = new Member();
        newMember.setName(data.getName());
        newMember.setEmail(data.getEmail());
        newMember.setRegistrationDate(LocalDate.now());
        newMember.setWhatsapp(data.getWhatsapp());
        newMember.setBirthDate(data.getBirthDate());
        newMember.setPlan(plan);
        newMember.setStatus(MemberStatus.PENDING);

        if (data.getHolderId() != null) {
            Member holder = memberRepo.findById(data.getHolderId())
                    .orElseThrow(() -> new ResourceNotFoundException("Aluno titular não encontrado com ID: " + data.getHolderId()));
            newMember.setHolder(holder);
        }

        newMember = memberRepo.save(newMember);

        if (newMember.getHolder() == null) {
            paymentService.generateCharge(newMember, LocalDate.now());
        }

        return new MemberResponseDTO(newMember);
    }

    @Transactional
    public MemberUpdateResponseDTO updateMember(Long id, MemberRegistrationDTO data) {
        Member member = this.getMemberById(id);
        Plan newPlan = planService.getById(data.getPlanId());

        boolean isNewPlanCouple = newPlan.getName().toLowerCase().contains("casal");

        if (member.getHolder() != null) {
            if (!isNewPlanCouple) {
                member.setHolder(null);
            }
        }

        if (!member.getPlan().getId().equals(newPlan.getId())) {
            paymentService.updatePendingChargesForPlanChange(member, newPlan.getPrice());
            member.setPlan(newPlan);

            if (!member.getDependents().isEmpty()) {
                for (Member dependent : member.getDependents()) {

                    if (!isNewPlanCouple) {
                        dependent.setHolder(null);
                    }

                    dependent.setPlan(newPlan);
                    memberRepo.save(dependent);
                }

                if (!isNewPlanCouple) {
                    member.getDependents().clear();
                }
            }
        }

        member.setName(data.getName());
        member.setEmail(data.getEmail());
        member.setWhatsapp(data.getWhatsapp());
        member.setBirthDate(data.getBirthDate());

        member = memberRepo.save(member);

        String message = "Aluno atualizado com sucesso.";
        return new MemberUpdateResponseDTO(message, member.getId());
    }

    @Transactional
    public MemberUpdateResponseDTO inactivateMember(Long memberId) {
        Member member = this.getMemberById(memberId);

        if (member.getHolder() == null && !member.getDependents().isEmpty()) {

            boolean hasActiveDependents = member.getDependents().stream()
                    .anyMatch(dep -> dep.getStatus() == MemberStatus.ACTIVE || dep.getStatus() == MemberStatus.PENDING);

            if (hasActiveDependents) {
                throw new BusinessRuleException(
                        "Não é possível inativar este titular. Ele possui dependentes ativos. Inative os dependentes primeiro ou promova-os a titulares mudando o plano."
                );
            }
        }
        paymentService.cancelPendingCharges(member);

        member.setInactivationDate(LocalDate.now());
        member.setStatus(MemberStatus.INACTIVE);
        memberRepo.save(member);

        String message = "Aluno inativado com sucesso. Ele não receberá mais cobranças a partir de agora.";
        return new MemberUpdateResponseDTO(message, member.getId());
    }

    public MemberUpdateResponseDTO activateMember(Long id) {
        Member member = this.getMemberById(id);

        if (member.getHolder() != null && member.getHolder().getStatus() != MemberStatus.ACTIVE) {
            throw new BusinessRuleException(
                    "Não é possível ativar este dependente pois o titular vinculado está inativo."
            );
        }

        member.setInactivationDate(null);
        member.setStatus(MemberStatus.ACTIVE);

        var charged = paymentService.generateCharge(member, LocalDate.now());

        if (charged != null) {
            member.setStatus(MemberStatus.PENDING);
        } else {
            member.setStatus(MemberStatus.ACTIVE);
        }

        memberRepo.save(member);

        String message = "Aluno reativado com sucesso. " + (charged != null ? "Uma nova cobrança foi gerada para o mês de retorno." : "O aluno já possuía cobrança para este mês.");

        return new MemberUpdateResponseDTO(message, member.getId());
    }

    private Member getMemberById(Long id) {
        return memberRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aluno não encontrado com ID: " + id));
    }
}
