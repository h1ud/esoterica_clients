package com.work.esotericaClients.dto.code;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiscountCodeConsumeRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String adminUsername;
}

