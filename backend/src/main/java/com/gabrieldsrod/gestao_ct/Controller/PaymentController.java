package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.PaymentClearenceDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PendingPaymentDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaidPaymentDTO;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/pending")
    public Page<PendingPaymentDTO> listPendingPayments(Pageable pageable) {
        return paymentService.listPending(pageable);
    }

    @GetMapping("/paid")
    public Page<PaidPaymentDTO> listPaidPayments(Pageable pageable) {
        return paymentService.listPaid(pageable);
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<PaymentReceiptDTO> registerPayment(@PathVariable Long id, @RequestBody PaymentClearenceDTO paymentMethod) {
        return ResponseEntity.ok(paymentService.registerPayment(id, paymentMethod.getPaymentMethod()));
    }
}
