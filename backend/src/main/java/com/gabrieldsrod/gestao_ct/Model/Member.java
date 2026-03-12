package com.gabrieldsrod.gestao_ct.Model;

import com.gabrieldsrod.gestao_ct.Enums.MemberStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
@Data
@EqualsAndHashCode(callSuper = true)
public class Member extends PersonalData {

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private MemberStatus status;

    @JoinColumn(name = "plan_id", nullable = false)
    @ManyToOne
    private Plan plan;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Column(name = "inactivation_date")
    private LocalDate inactivationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "holder_id")
    private Member holder;

    @OneToMany(mappedBy = "holder", cascade = CascadeType.ALL)
    private List<Member> dependents = new ArrayList<>();
}
