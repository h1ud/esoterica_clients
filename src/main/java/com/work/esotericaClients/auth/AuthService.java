package com.work.esotericaClients.auth;

import org.springframework.stereotype.Service;

import com.work.esotericaClients.auth.dto.AuthResponse;
import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.service.ClientService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final ClientService service;
    private final JwtService jwtService;

    public AuthResponse register(Client client){
        Client savedClient=service.createClient(client);
        String token=
            jwtService.generateToken(savedClient.getId());
        return new AuthResponse(token);
    }
/* 
    public void register(Client client client){
        Client client=new Client();

        client.setName(request.name());
        client.setPasswordHash(request.passwordHash());
        client.setDni(request.Dni());
        client.setBirthDay(request.birthDay());

        service.createClient(client);

    }*/
}
