package com.work.esotericaClients.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository){
        this.repository= repository;
    }

    public List<Client> getName(){
        return repository.findAll();
    }

    public List<Client> getDni(){
        return repository.findAll();
    }

}
