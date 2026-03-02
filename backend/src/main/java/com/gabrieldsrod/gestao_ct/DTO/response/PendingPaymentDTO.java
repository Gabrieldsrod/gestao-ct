package com.gabrieldsrod.gestao_ct.DTO.response;

import com.gabrieldsrod.gestao_ct.Model.MemberPayment;
import com.gabrieldsrod.gestao_ct.Utils.DateUtils;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PendingPaymentDTO {

    private Long paymentId;
    private Long memberId;
    private String memberName;
    private String memberEmail;
    private String memberPhone;
    private String planName;
    private String dueDate; // Formato "dd/MM/yyyy"
    private BigDecimal amountDue;

    public static PendingPaymentDTO fromEntity(MemberPayment payment){
        PendingPaymentDTO dto = new PendingPaymentDTO();
        dto.setPaymentId(payment.getId());
        dto.setMemberId(payment.getMember().getId());
        dto.setMemberName(payment.getMember().getName());
        dto.setMemberEmail(payment.getMember().getEmail());
        dto.setMemberPhone(payment.getMember().getWhatsapp());
        dto.setPlanName(payment.getMember().getPlan().getName());
        dto.setDueDate(payment.getDueDate().format(DateUtils.BR_FORMATTER));    // Formata a data para "dd/MM/yyyy"
        dto.setAmountDue(payment.getAmountCharged());
        return dto;
    }
}
