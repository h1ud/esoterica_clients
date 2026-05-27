package com.work.esotericaClients.dto.code;

import com.work.esotericaClients.entity.DiscountCodeStatus;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiscountCodeResponse {

    private Long id;
    private String code;
    private LocalDateTime generatedAt;
    private LocalDateTime expiresAt;
    private DiscountCodeStatus status;
    private LocalDateTime usedAt;
    private Long clientId;
    private Long discountId;
}

