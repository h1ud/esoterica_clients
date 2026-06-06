package com.work.esotericaClients.dto;

import java.time.LocalDate;

public record CodeActivationResponse(
    Long id,
    Long codeId,
    String codeTitle,
    Integer productQuantity,
    LocalDate activatedAt
) {}
