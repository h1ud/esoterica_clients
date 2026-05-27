package com.work.esotericaClients.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientUpdateRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @NotBlank
    @Size(min = 7, max = 20)
    private String phone;
}

