package com.work.esotericaClients.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="code")
public class Code {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable=true, length=120)
    private String title;

    @Column(nullable=true, length=500)
    private String description;

    @Column(nullable=true, precision=3, scale=1)
    private BigDecimal valueCode;

    @Column(nullable=true, precision=3, scale=1)
    private BigDecimal discountCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable=true, length=20)
    private CodeVisibility visibility;

    @Column(nullable = false)
    private Boolean isActive;

    @Column(nullable = false)
    private Boolean isUsed;

    @Column(nullable = false)
    private LocalDate expiration;

    @Column(nullable = false, updatable = false)
    private LocalDate createdAt;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy="code")
    private List<Client> clients;
}
