package com.work.esotericaClients.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.work.esotericaClients.domain.entity.Client;

public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findByName(String name);
    Optional<Client> findByDni(String dni);
}
