package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaidPaymentDTO {

    private Long id;
    private String memberName;
    private BigDecimal amount;
    private String paymentDate;

    public static PaidPaymentDTO fromEntity(MemberPayment payment) {
        PaidPaymentDTO dto = new PaidPaymentDTO();
        dto.setId(payment.getId());
        dto.setMemberName(payment.getMember().getName());
        dto.setAmount(payment.getAmountPaid());
        dto.setPaymentDate(payment.getPaymentDate().format(DateUtils.BR_FORMATTER));    // Formata a data para "dd/MM/yyyy"
        return dto;
    }
}
