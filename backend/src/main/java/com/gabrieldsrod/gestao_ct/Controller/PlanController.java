package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.NewPlanDTO;
import com.gabrieldsrod.gestao_ct.DTO.request.PlanUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PlanResponseDTO;
import com.gabrieldsrod.gestao_ct.Service.PlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public List<PlanResponseDTO> getPlans() {
        return planService.getAll();
    }

    @GetMapping("/count-members")
    public List<PlanResponseDTO> getPlansWithMembers() {
        return planService.getAllPlans();
    }

    @PostMapping
    public ResponseEntity<PlanResponseDTO> createPlan(@RequestBody NewPlanDTO data) {
        PlanResponseDTO newPlan = planService.createPlan(data);
        return ResponseEntity.status(HttpStatus.CREATED).body(newPlan);
    }

    @PatchMapping("/{id}")
    public PlanResponseDTO updatePlan(@PathVariable Long id, @RequestBody PlanUpdateDTO planDTO) {
        return planService.updatePlan(id, planDTO);
    }
}
