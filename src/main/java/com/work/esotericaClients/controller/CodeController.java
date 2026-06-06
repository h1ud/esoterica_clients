package com.work.esotericaClients.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.auth.AdminAuthorizationService;
import com.work.esotericaClients.auth.SessionAuthorizationService;
import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.domain.entity.Code;
import com.work.esotericaClients.dto.CodeActivationResponse;
import com.work.esotericaClients.dto.OfferActivationRequest;
import com.work.esotericaClients.dto.OfferActivationResponse;
import com.work.esotericaClients.service.CodeService;

@RestController
@RequestMapping("/api/code")
public class CodeController {

    private final CodeService service;
    private final AdminAuthorizationService adminAuthorizationService;
    private final SessionAuthorizationService sessionAuthorizationService;

    public CodeController(
        CodeService service,
        AdminAuthorizationService adminAuthorizationService,
        SessionAuthorizationService sessionAuthorizationService
    ){
        this.service=service;
        this.adminAuthorizationService=adminAuthorizationService;
        this.sessionAuthorizationService = sessionAuthorizationService;
    }

    @PostMapping
    public Code createdCode(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody Code code){
            adminAuthorizationService.requireAdmin(authorizationHeader);
            return service.createCode(code);
    }
    
    @PutMapping("/{id}")
    public Code updateCode (
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody Code code){
            adminAuthorizationService.requireAdmin(authorizationHeader);
            return service.updateCode(id, code);
    }

    @DeleteMapping("/{id}")
    public void deleteCode(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @PathVariable Long id){
            adminAuthorizationService.requireAdmin(authorizationHeader);
            service.deleteCode(id);
    }
    @GetMapping
    public List<Code> getCodes(){
        return service.getCodes();
    }

    @PostMapping("/{id}/activate")
    public OfferActivationResponse activateCode(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @PathVariable Long id,
        @RequestBody(required = false) OfferActivationRequest request){
            Client client = sessionAuthorizationService.requireClient(authorizationHeader);
            return service.activateCode(id, client, request);
    }

    @GetMapping("/activations/me")
    public List<CodeActivationResponse> getMyActivations(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader){
            Client client = sessionAuthorizationService.requireClient(authorizationHeader);
            return service.getActivationsForClient(client.getId());
    }

    @GetMapping("/title/{title}")
    public List<Code> getByTitle(
        @PathVariable String title){
            return service.getByTitle(title);
    }

    @GetMapping("/createdAt/{createdAt}")
    public List<Code> getByCreatedAt(
        @PathVariable LocalDate createdAt){
            return service.getByCreatedAt(createdAt);
    }

    @GetMapping("/expiration/{expiration}")
    public List<Code> getByExpiration(
        @PathVariable LocalDate expiration){
            return service.getByExpiration(expiration);
    }
}
