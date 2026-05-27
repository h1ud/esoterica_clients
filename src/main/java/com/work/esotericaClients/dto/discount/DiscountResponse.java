package com.work.esotericaClients.dto.discount;

import com.work.esotericaClients.entity.DiscountType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiscountResponse {

    private Long id;
    private String title;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer stockLimit;
    private Integer usedCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
}

