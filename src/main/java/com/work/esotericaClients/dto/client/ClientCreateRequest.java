package com.work.esotericaClients.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientCreateRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String fullName;

    @NotBlank
    @Size(min = 8, max = 12)
    private String dni;

    @NotBlank
    @Email
    @Size(max = 160)
    private String email;

    @NotBlank
    @Size(min = 7, max = 20)
    private String phone;

    @NotBlank
    @Size(min = 6, max = 40)
    private String password;

    @NotNull
    @Past
    private LocalDate birthdayDate;
}

