package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.PaymentClearenceDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PendingPaymentDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/pending")
    public List<PendingPaymentDTO> listPendingPayments() {
        return paymentService.listPending();
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<PaymentReceiptDTO> registerPayment(@PathVariable Long id, @RequestBody PaymentClearenceDTO metodoPagamento) {
        return ResponseEntity.ok(paymentService.registerPayment(id, metodoPagamento.getPaymentMethod()));
    }
}

