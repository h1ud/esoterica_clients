package com.work.esotericaClients.repository;

import com.work.esotericaClients.entity.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByDni(String dni);

    Optional<Client> findByEmail(String email);
}

