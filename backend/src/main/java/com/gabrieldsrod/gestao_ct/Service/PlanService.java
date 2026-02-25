package com.gabrieldsrod.gestao_ct.Service;

import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlanService {

    private final PlanRepository planRepo;

    public PlanService(PlanRepository planRepo) {
        this.planRepo = planRepo;
    }

    public List<PlanResponseDTO> getAll() {
        return planRepo.findAll().stream()
                .map(PlanResponseDTO::new).toList();
    }
}
