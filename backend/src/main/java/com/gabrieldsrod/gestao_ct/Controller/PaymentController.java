package com.gabrieldsrod.gestao_ct.Controller;

import com.gabrieldsrod.gestao_ct.DTO.request.PaymentClearenceDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentReceiptDTO;
import com.gabrieldsrod.gestao_ct.DTO.response.PaymentResponseDTO;
import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import com.gabrieldsrod.gestao_ct.Service.PaymentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/v1/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<Page<PaymentResponseDTO>> getAllPayments(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) Long memberId,
            @RequestParam(required = false) PaymentStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("dueDate"));

        Page<PaymentResponseDTO> payments = paymentService.searchPayments(startDate, endDate, memberId, status, pageable);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{id}/register")
    public ResponseEntity<PaymentReceiptDTO> registerPayment(@PathVariable Long id, @RequestBody PaymentClearenceDTO paymentMethod) {
        return ResponseEntity.ok(paymentService.registerPayment(id, paymentMethod.getPaymentMethod()));
    }
}
