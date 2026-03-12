package com.gabrieldsrod.gestao_ct.Model;

import com.gabrieldsrod.gestao_ct.Enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "member_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberPayment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;       // Data de vencimento da mensalidade
    @Column(name = "amount_charged",nullable = false , precision = 19, scale = 2)
    private BigDecimal amountCharged;

    // Campos para controle de pagamento, preenchidos quando o pagamento for realizado
    @Column(name = "payment_date")
    private LocalDate paymentDate;
    @Column(name = "amount_paid", precision = 19, scale = 2)
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus status = PaymentStatus.PENDING;

    @OneToOne
    @JoinColumn(name = "transaction_id", unique = true)
    private Transaction transaction;
}
