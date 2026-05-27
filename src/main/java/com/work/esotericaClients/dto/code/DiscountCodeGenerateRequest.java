package com.work.esotericaClients.dto.code;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountCodeGenerateRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long discountId;
}
