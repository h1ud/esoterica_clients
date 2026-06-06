package com.work.esotericaClients.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "store_settings")
public class StoreSettings {

    @Id
    private Long id;

    @Column(nullable = false)
    private Integer vipRequiredProducts;

    @Column(nullable = false)
    private LocalDate updatedAt;
}
