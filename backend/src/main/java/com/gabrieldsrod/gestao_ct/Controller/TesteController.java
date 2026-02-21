package com.gabrieldsrod.gestao_ct.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/v1/api/teste")
public class TesteController {

    @GetMapping
    public ResponseEntity<?> teste() {
        return ResponseEntity.ok().body(Map.of(
                "mensagem", "Endpoint de teste funcionando corretamente",
                "status", "OK",
                "timestamp", LocalDate.now()
        ));
    }
}
