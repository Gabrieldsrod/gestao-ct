package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;

import java.math.BigDecimal;

public record PaymentResponseDTO(
        Long id,
        Long memberId,
        String memberName,
        String memberEmail,
        String memberPhone,
        String planName,
        String dueDate,
        String paymentDate,
        BigDecimal amountDue,
        BigDecimal amountPaid,
        String status
) {
    public PaymentResponseDTO(MemberPayment payment) {
        this(
                payment.getId(),
                payment.getMember().getId(),
                payment.getMember().getName(),
                payment.getMember().getEmail(),
                payment.getMember().getWhatsapp(),
                payment.getMember().getPlan() != null ? payment.getMember().getPlan().getName() : "Sem plano",
                payment.getDueDate() != null ? payment.getDueDate().format(DateUtils.BR_FORMATTER_DATE) : null,
                payment.getPaymentDate() != null ? payment.getPaymentDate().format(DateUtils.BR_FORMATTER_DATETIME) : null,
                payment.getAmountCharged(),
                payment.getAmountPaid(),
                payment.getStatus().name()
        );
    }
}
