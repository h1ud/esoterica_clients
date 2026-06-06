package com.work.esotericaClients.dto;

import java.time.LocalDate;

public record AdminClientUpdateRequest(
    String name,
    String dni,
    LocalDate birthdayDate,
    String role,
    Integer weeklyProductCount,
    Boolean vip
) {}
