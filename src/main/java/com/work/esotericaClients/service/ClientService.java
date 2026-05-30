package com.work.esotericaClients.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repository;

    public ClientService(ClientRepository repository){
        this.repository= repository;
    }

    public List<Client> getByName(String name){
        return repository.findByName(name);
    }

    public Optional<Client> getByDni(String dni){
        return repository.findByDni(dni);
    }

    public Client createClient(Client client){
        return repository.save(client)  ;
    }

    public void deleteClient(Long id){
        repository.deleteById(id);
    }

    public Client updateClient(Long id, Client client){
        client.setId(id);
        return repository.save(client);
    }

    //frond-end screen
    public List<Client> getClients(){
        return repository.findAll();
    }
}
