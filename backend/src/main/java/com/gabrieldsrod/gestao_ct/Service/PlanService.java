package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.NewPlanDTO;
import com.gabrieldsrod.gestao_ct.DTO.request.PlanUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.MemberRepository;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepo;

    private final MemberRepository memberRepo;

    public PlanService(PlanRepository planRepo, MemberRepository memberRepo) {
        this.planRepo = planRepo;
        this.memberRepo = memberRepo;
    }

    public List<PlanResponseDTO> getAll() {
        return planRepo.findAll(Sort.by(Sort.Direction.ASC, "id")).stream() // Pode mudar no futuro para ordenação direta no banco de dados, mas por enquanto é mais simples fazer aqui
                .map(PlanResponseDTO::new).toList();
    }

    public List<PlanResponseDTO> getAllPlans() {
        List<Plan> plans = planRepo.findAll();
        return plans.stream()
                .map(plan -> {
                    PlanResponseDTO dto = new PlanResponseDTO(plan);
                    Long activeMembers = memberRepo.countActiveMembersByPlanId(plan.getId());
                    return new PlanResponseDTO(
                            dto.id(),
                            dto.name(),
                            dto.price(),
                            activeMembers,
                            dto.lastUpdated()
                    );
                })
                .toList();
    }

    public Plan getById(Long id) {
        return planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + id));
    }

    public PlanResponseDTO createPlan(NewPlanDTO planDTO) {
        Plan newPlan = new Plan();
        newPlan.setName(planDTO.name());
        newPlan.setPrice(planDTO.price());

        newPlan = planRepo.save(newPlan);

        return new PlanResponseDTO(newPlan);
    }

    public PlanResponseDTO updatePlan(Long id, PlanUpdateDTO planDTO) {
        Plan existingPlan = getById(id);

        if (planDTO.name() != null) {
            existingPlan.setName(planDTO.name());
        }
        if (planDTO.price() != null) {
            existingPlan.setPrice(planDTO.price());
        }

        Plan updatedPlan = planRepo.save(existingPlan);
        return new PlanResponseDTO(updatedPlan);
    }
}
