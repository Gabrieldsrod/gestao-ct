package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.FeeUpdateDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.FeeResponseDTO;
import com.gabrieldsrod.gestao_ct.Service.FeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/fees")
@CrossOrigin(origins = "*")
public class FeeController {

    private final FeeService feeService;

    public FeeController(FeeService feeService) {
        this.feeService = feeService;
    }

    @GetMapping
    public ResponseEntity<List<FeeResponseDTO>> listFees() {
        List<FeeResponseDTO> fees = feeService.getAllFees();
        return ResponseEntity.ok(fees);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFee(@PathVariable Long id, @RequestBody FeeUpdateDTO data) {
        feeService.updateFee(id, data);
        return ResponseEntity.ok().build();
    }
}
