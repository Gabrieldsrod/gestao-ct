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
    private Integer preferredPaymentDay;
    private String dueDate; // Formato "dd/MM/yyyy"
    private BigDecimal amountDue;

    public static PendingPaymentDTO fromEntity(MemberPayment pagamento){
        PendingPaymentDTO dto = new PendingPaymentDTO();
        dto.setPaymentId(pagamento.getId());
        dto.setMemberId(pagamento.getMember().getId());
        dto.setMemberName(pagamento.getMember().getName());
        dto.setMemberEmail(pagamento.getMember().getEmail());
        dto.setMemberPhone(pagamento.getMember().getWhatsapp());
        dto.setPlanName(pagamento.getMember().getPlan().getName());
        dto.setPreferredPaymentDay(pagamento.getMember().getPreferredPaymentDay());
        dto.setDueDate(pagamento.getDueDate().format(DateUtils.BR_FORMATTER));    // Formata a data para "dd/MM/yyyy"
        dto.setAmountDue(pagamento.getAmountCharged());
        return dto;
    }
}
