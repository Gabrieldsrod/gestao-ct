package com.gabrieldsrod.gestao_ct.Model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "members")
@Data
@EqualsAndHashCode(callSuper = true)
public class Member extends PersonalData {

    @Column(name = "preferred_payment_day", nullable = false)
    private Integer preferredPaymentDay;

    @Column(nullable = false)
    private Boolean active = true;

    @JoinColumn(name = "plan_id", nullable = false)
    @ManyToOne
    private Plan plan;
}
