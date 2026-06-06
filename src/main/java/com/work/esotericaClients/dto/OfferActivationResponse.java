package com.work.esotericaClients.dto;

public record OfferActivationResponse(
    CodeActivationResponse activation,
    ClientSummaryResponse client,
    Boolean vipUnlocked,
    String message
) {}
