package com.gabrieldsrod.gestao_ct.Model;

import com.gabrieldsrod.gestao_ct.Enums.PaymentMethod;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Entity
@Table(name = "payment_fees")
@Data
@EqualsAndHashCode(callSuper = true)
public class PaymentFee extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", unique = true, nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "percentage_fee", nullable = false)
    private BigDecimal percentageFee = BigDecimal.ZERO;

    @Column(name = "fixed_fee", nullable = false)
    private BigDecimal fixedFee = BigDecimal.ZERO;

    @Column(name = "days_to_receive")
    private Integer daysToReceive = 0;
}
