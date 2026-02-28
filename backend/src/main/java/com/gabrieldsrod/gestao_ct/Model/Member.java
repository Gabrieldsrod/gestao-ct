package com.gabrieldsrod.gestao_ct.Model;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Data
@EqualsAndHashCode(callSuper = true)
public class Member extends PersonalData {

    @Column(name = "preferred_payment_day", nullable = false)
    private Integer preferredPaymentDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @JoinColumn(name = "plan_id", nullable = false)
    @ManyToOne
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titular_id")
    private Member holder;

    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL)
    private List<Member> dependents = new ArrayList<>();
}
