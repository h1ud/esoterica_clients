package com.work.esotericaClients.dto.client;

import com.work.esotericaClients.entity.ClientStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ClientResponse {

    private Long id;
    private String fullName;
    private String dni;
    private String email;
    private String phone;
    private LocalDate birthdayDate;
    private ClientStatus status;
    private LocalDateTime createdAt;
}

