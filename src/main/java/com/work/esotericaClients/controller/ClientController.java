package com.work.esotericaClients.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.dto.AdminClientUpdateRequest;
import com.work.esotericaClients.dto.ClientSummaryResponse;
import com.work.esotericaClients.service.ClientService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.work.esotericaClients.auth.AdminAuthorizationService;
import com.work.esotericaClients.auth.SessionAuthorizationService;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    public final ClientService service;
    private final AdminAuthorizationService adminAuthorizationService;
    private final SessionAuthorizationService sessionAuthorizationService;

    public ClientController(
        ClientService service,
        AdminAuthorizationService adminAuthorizationService,
        SessionAuthorizationService sessionAuthorizationService
    ){
        this.service=service;
        this.adminAuthorizationService=adminAuthorizationService;
        this.sessionAuthorizationService = sessionAuthorizationService;
    }
    /*
    @PostMapping
    public Client createCode(
        @RequestBody Client client){
            return service.createClient(client);
    }*/

    @PutMapping("/{id}")
    public ClientSummaryResponse updateClient(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody AdminClientUpdateRequest client){
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.toSummary(service.updateClient(id, client));
    }
    
    @DeleteMapping("/{id}")
    public void deleteClient(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @PathVariable Long id){
            adminAuthorizationService.requireAdmin(authorizationHeader);
            service.deleteClient(id);
    }

    @GetMapping
    public List<ClientSummaryResponse> getClients(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.getClientSummaries();
    }

    @GetMapping("/me")
    public ClientSummaryResponse getMe(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
        Client client = sessionAuthorizationService.requireClient(authorizationHeader);
        return service.toSummary(client);
    }

    @GetMapping("/name/{name}")
    public List<ClientSummaryResponse> getByName(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @PathVariable String name){
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.getSummaryByName(name);
    }
}
