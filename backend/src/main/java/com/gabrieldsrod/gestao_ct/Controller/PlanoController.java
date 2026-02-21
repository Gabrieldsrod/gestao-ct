package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.response.PlanoDTO;
import com.gabrieldsrod.gestao_ct.Model.Plano;
import com.gabrieldsrod.gestao_ct.Repository.PlanoRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/api/planos")
public class PlanoController {

    private final PlanoRepository planoRepo;

    public PlanoController(PlanoRepository planoRepo) {
        this.planoRepo = planoRepo;
    }

    @GetMapping
    public List<PlanoDTO> getAllPlanos() {
        return planoRepo.findAll().stream()
                                .map(PlanoDTO::new)
                                .toList();
    }
}
