package com.work.esotericaClients.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.service.ClientService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/clients")
public class ClientController {

    public final ClientService service;

    public ClientController(ClientService service){
        this.service=service;
    }
    /*
    @PostMapping
    public Client createCode(
        @RequestBody Client client){
            return service.createClient(client);
    }*/

    @PutMapping("/{id}")
    public Client updateClient(
        @PathVariable Long id,
        @RequestBody Client client){
        return service.updateClient(id, client);
    }
    
    @DeleteMapping("/{id}")
    public void deleteClient(
        @PathVariable Long id){
            service.deleteClient(id);
    }

    @GetMapping
    public List<Client> getClients(){
        return service.getClients();
    }

    @GetMapping("/name/{name}")
    public List<Client> getByName(
        @PathVariable String name){
            return service.getByName(name);
    }
}
