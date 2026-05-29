package com.work.esotericaClients.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Code {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=true, length=120)
    private String title;

    @Column(nullable=true, length=500)
    private String description;

    @Column(nullable=true, precision=3, scale=1)
    private BigDecimal value_code;

    @Column(nullable=true, precision=3, scale=1)
    private BigDecimal discount_code;

    @Enumerated(EnumType.STRING)
    @Column(nullable=true, length=20)
    private CodeVisibility visibility;

}
