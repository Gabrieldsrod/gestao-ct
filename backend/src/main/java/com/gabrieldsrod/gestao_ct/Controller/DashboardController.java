package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.response.DashboardSummaryDTO;
import com.gabrieldsrod.gestao_ct.Service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        return ResponseEntity.ok(dashboardService.getDashboardSummary());
    }
}
