package com.work.esotericaClients.dto;

import java.time.LocalDate;

public record ClientSummaryResponse(
    Long id,
    String name,
    String dni,
    LocalDate birthdayDate,
    String role,
    Integer weeklyProductCount,
    Boolean vip,
    LocalDate vipSince,
    Integer vipRequiredProducts,
    Integer vipRemainingProducts,
    Long usedOffersCount
) {}
