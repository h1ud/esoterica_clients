package com.work.esotericaClients.auth;

import com.work.esotericaClients.repository.ClientRepository;

import java.util.Optional;

import org.springframework.stereotype.Service;


import com.work.esotericaClients.auth.dto.AuthResponse;
import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.dto.ClientSummaryResponse;
import com.work.esotericaClients.service.ClientService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final ClientRepository clientRepository;
    private final ClientService service;
    private final JwtService jwtService;


    public AuthResponse register(Client client){
        client.setRole("client");
        Client savedClient=service.createClient(client);
        String token=
            jwtService.generateToken(savedClient.getId());
        return buildAuthResponse(token, savedClient);
    }

    public AuthResponse login(Client request){
        Optional<Client> optionalClient = clientRepository.findByDni(request.getDni());
        if (optionalClient.isEmpty()) {
            return null;
        }
        Client client=optionalClient.get();
        if(!client.getPasswordHash().equals(request.getPasswordHash())){
            return null;
        }
        String token=
            jwtService.generateToken(client.getId());
        return buildAuthResponse(token, client);
    }

    private AuthResponse buildAuthResponse(String token, Client client) {
        ClientSummaryResponse summary = service.toSummary(client);
        return new AuthResponse(
            token,
            summary.id(),
            summary.name(),
            summary.dni(),
            summary.birthdayDate(),
            summary.role(),
            summary.weeklyProductCount(),
            summary.vip(),
            summary.vipSince(),
            summary.vipRequiredProducts(),
            summary.vipRemainingProducts(),
            summary.usedOffersCount()
        );
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
