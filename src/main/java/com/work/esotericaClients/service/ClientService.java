package com.work.esotericaClients.service;

import com.work.esotericaClients.dto.client.ClientCreateRequest;
import com.work.esotericaClients.dto.client.ClientResponse;
import com.work.esotericaClients.dto.client.ClientUpdateRequest;
import com.work.esotericaClients.entity.Client;
import com.work.esotericaClients.entity.ClientStatus;
import com.work.esotericaClients.exception.BadRequestException;
import com.work.esotericaClients.exception.ResourceNotFoundException;
import com.work.esotericaClients.repository.ClientRepository;
import java.util.Objects;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Transactional
    public ClientResponse register(ClientCreateRequest request) {
        if (clientRepository.findByDni(request.getDni()).isPresent()) {
            throw new BadRequestException("DNI already registered");
        }
        if (clientRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already registered");
        }

        Client client = Client.builder()
                .fullName(request.getFullName())
                .dni(request.getDni())
                .email(request.getEmail())
                .phone(request.getPhone())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .birthdayDate(request.getBirthdayDate())
                .status(ClientStatus.ACTIVE)
                .build();

        Client saved = clientRepository.save(client);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<ClientResponse> findAll(Pageable pageable) {
        return clientRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public ClientResponse findById(Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));
        return toResponse(client);
    }

    @Transactional
    public ClientResponse update(Long id, ClientUpdateRequest request) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found"));

        clientRepository.findByEmail(request.getEmail())
                .filter(found -> !Objects.equals(found.getId(), client.getId()))
                .ifPresent(found -> {
                    throw new BadRequestException("Email already registered");
                });

        client.setFullName(request.getFullName());
        client.setEmail(request.getEmail());
        client.setPhone(request.getPhone());

        Client saved = clientRepository.save(client);
        return toResponse(saved);
    }

    private ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .dni(client.getDni())
                .email(client.getEmail())
                .phone(client.getPhone())
                .birthdayDate(client.getBirthdayDate())
                .status(client.getStatus())
                .createdAt(client.getCreatedAt())
                .build();
    }
}

