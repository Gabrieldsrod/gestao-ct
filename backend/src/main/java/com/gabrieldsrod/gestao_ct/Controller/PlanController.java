package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Repository.PlanRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanRepository planRepo;

    public PlanController(PlanRepository planRepo) {
        this.planRepo = planRepo;
    }

    @GetMapping
    public List<PlanResponseDTO> getAllPlanos() {
        return planRepo.findAll().stream()
                .map(PlanResponseDTO::new).toList();
    }
}
