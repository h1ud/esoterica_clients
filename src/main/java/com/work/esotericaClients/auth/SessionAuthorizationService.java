package com.work.esotericaClients.auth;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.repository.ClientRepository;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SessionAuthorizationService {
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final ClientRepository clientRepository;

    public Client requireClient(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token requerido");
        }

        try {
            Long clientId = jwtService.extractClientId(authorizationHeader.substring(BEARER_PREFIX.length()));
            return clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida"));
        } catch (ResponseStatusException exception) {
            throw exception;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token invalido");
        }
    }
}
