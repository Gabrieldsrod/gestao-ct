package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.PlanUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Service.PlanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/plans")
@CrossOrigin(origins = "*")
public class PlanController {

    private final PlanService planService;

    public PlanController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public List<PlanResponseDTO> getAllPlans() {
        return planService.getAll();
    }

    @PatchMapping("/{id}")
    public PlanResponseDTO updatePlan(@PathVariable Long id, @RequestBody PlanUpdateDTO planDTO) {
        return planService.updatePlan(id, planDTO);
    }
}
