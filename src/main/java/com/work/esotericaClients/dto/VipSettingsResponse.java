package com.work.esotericaClients.dto;

import java.time.LocalDate;

public record VipSettingsResponse(
    Integer vipRequiredProducts,
    LocalDate updatedAt
) {}
