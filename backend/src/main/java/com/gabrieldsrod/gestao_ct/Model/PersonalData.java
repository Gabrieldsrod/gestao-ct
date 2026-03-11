package com.gabrieldsrod.gestao_ct.Model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public abstract class PersonalData extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String email;

    @Column(length = 20, nullable = false)
    private String whatsapp;

    @Column(nullable = false)
    private LocalDate birthDate;
}
