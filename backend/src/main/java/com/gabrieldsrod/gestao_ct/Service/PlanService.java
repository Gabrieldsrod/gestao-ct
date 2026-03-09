package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.request.PlanUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Model.Plan;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepo;

    public PlanService(PlanRepository planRepo) {
        this.planRepo = planRepo;
    }

    public List<PlanResponseDTO> getAll() {
        return planRepo.findAll(Sort.by(Sort.Direction.ASC, "id")).stream() // Pode mudar no futuro para ordenação direta no banco de dados, mas por enquanto é mais simples fazer aqui
                .map(PlanResponseDTO::new).toList();
    }

    public Plan getById(Long id) {
        return planRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Plano não encontrado com ID: " + id));
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
