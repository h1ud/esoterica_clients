package com.work.esotericaClients.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.dto.AdminClientUpdateRequest;
import com.work.esotericaClients.dto.ClientSummaryResponse;
import com.work.esotericaClients.repository.CodeActivationRepository;
import com.work.esotericaClients.repository.ClientRepository;

@Service
public class ClientService {

    private final ClientRepository repository;
    private final StoreSettingsService settingsService;
    private final CodeActivationRepository activationRepository;

    public ClientService(
        ClientRepository repository,
        StoreSettingsService settingsService,
        CodeActivationRepository activationRepository
    ){
        this.repository= repository;
        this.settingsService = settingsService;
        this.activationRepository = activationRepository;
    }

    public List<Client> getByName(String name){
        return repository.findByName(name);
    }

    public Optional<Client> getByDni(String dni){
        return repository.findByDni(dni);
    }

    public Client createClient(Client client){
        if (client.getRole() == null || client.getRole().isBlank()) {
            client.setRole("client");
        }
        if (client.getWeeklyProductCount() == null) {
            client.setWeeklyProductCount(0);
        }
        if (client.getVip() == null) {
            client.setVip(false);
        }
        return repository.save(client)  ;
    }

    public void deleteClient(Long id){
        repository.deleteById(id);
    }

    public Client updateClient(Long id, AdminClientUpdateRequest request){
        Client client = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));

        if (request.name() != null && !request.name().isBlank()) {
            client.setName(request.name().trim());
        }

        if (request.dni() != null && !request.dni().isBlank()) {
            client.setDni(request.dni().trim());
        }

        if (request.birthdayDate() != null) {
            client.setBirthdayDate(request.birthdayDate());
        }

        if (request.role() != null && !request.role().isBlank()) {
            String role = request.role().trim().toLowerCase();
            if (!role.equals("admin") && !role.equals("client")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rol no permitido");
            }
            client.setRole(role);
        }

        if (request.weeklyProductCount() != null) {
            client.setWeeklyProductCount(Math.max(0, request.weeklyProductCount()));
        }

        if (request.vip() != null) {
            updateVipStatus(client, request.vip());
        }

        return repository.save(client);
    }

    //frond-end screen
    public List<Client> getClients(){
        return repository.findAll();
    }

    public List<ClientSummaryResponse> getClientSummaries(){
        return repository.findAll().stream()
            .map(this::toSummary)
            .toList();
    }

    public List<ClientSummaryResponse> getSummaryByName(String name){
        return repository.findByName(name).stream()
            .map(this::toSummary)
            .toList();
    }

    public ClientSummaryResponse toSummary(Client client) {
        int requiredProducts = settingsService.getSettings().getVipRequiredProducts();
        int productCount = client.getWeeklyProductCount() == null ? 0 : client.getWeeklyProductCount();
        int remaining = Math.max(requiredProducts - productCount, 0);
        long usedOffersCount = activationRepository.countByClientId(client.getId());

        return new ClientSummaryResponse(
            client.getId(),
            client.getName(),
            client.getDni(),
            client.getBirthdayDate(),
            client.getRole(),
            productCount,
            Boolean.TRUE.equals(client.getVip()),
            client.getVipSince(),
            requiredProducts,
            remaining,
            usedOffersCount
        );
    }

    public void updateVipStatus(Client client, boolean vip) {
        client.setVip(vip);
        client.setVipSince(vip ? Optional.ofNullable(client.getVipSince()).orElse(LocalDate.now()) : null);
    }
}
