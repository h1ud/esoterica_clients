package com.work.esotericaClients.auth.dto;

import java.time.LocalDate;

public record AuthResponse(
    String token,
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
){}
