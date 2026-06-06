package com.work.esotericaClients.auth.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.auth.AuthService;
import com.work.esotericaClients.auth.dto.AuthResponse;
import com.work.esotericaClients.domain.entity.Client;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController


public class AuthController {

    private final AuthService authService;
    
    @PostMapping("/register")
    public AuthResponse register(
        @RequestBody Client client){
            return authService.register(client);
    }

    @PostMapping("/login")
    public AuthResponse login(
        @RequestBody Client client){
            return authService.login(client);
        }
}
