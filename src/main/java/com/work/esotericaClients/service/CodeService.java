package com.work.esotericaClients.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.work.esotericaClients.domain.entity.Client;
import com.work.esotericaClients.domain.entity.Code;
import com.work.esotericaClients.domain.entity.CodeActivation;
import com.work.esotericaClients.dto.CodeActivationResponse;
import com.work.esotericaClients.dto.OfferActivationRequest;
import com.work.esotericaClients.dto.OfferActivationResponse;
import com.work.esotericaClients.repository.ClientRepository;
import com.work.esotericaClients.repository.CodeActivationRepository;
import com.work.esotericaClients.repository.CodeRepository;

@Service
public class CodeService {
    private final CodeRepository repository;
    private final ClientRepository clientRepository;
    private final CodeActivationRepository activationRepository;
    private final ClientService clientService;

    public CodeService(
        CodeRepository repository,
        ClientRepository clientRepository,
        CodeActivationRepository activationRepository,
        ClientService clientService
    ){
        this.repository=repository;
        this.clientRepository = clientRepository;
        this.activationRepository = activationRepository;
        this.clientService = clientService;
    }

    public List<Code> getByTitle(String title){
        return repository.findByTitle(title);
    }

    public List<Code> getByCreatedAt(LocalDate createdAt){
        return repository.findByCreatedAt(createdAt);
    }

    public List<Code> getByExpiration(LocalDate expiration){
        return repository.findByExpiration(expiration);
    }

    public Code createCode(Code code){
        return repository.save(code);
    }

    public void deleteCode(Long id){
        repository.deleteById(id);
    }

    public Code updateCode(Long id, Code code){
        code.setId(id);
        return repository.save(code);
    }

    @Transactional
    public OfferActivationResponse activateCode(Long codeId, Client sessionClient, OfferActivationRequest request) {
        Code code = repository.findById(codeId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código no encontrado"));

        if (!Boolean.TRUE.equals(code.getIsActive()) || Boolean.TRUE.equals(code.getIsUsed())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La oferta no está disponible");
        }

        if (code.getExpiration().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La oferta ya venció");
        }

        int productQuantity = request == null || request.productQuantity() == null ? 1 : request.productQuantity();
        if (productQuantity < 1 || productQuantity > 50) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cantidad de productos debe estar entre 1 y 50");
        }

        Client client = clientRepository.findById(sessionClient.getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesion invalida"));

        if (activationRepository.existsByClientIdAndCodeId(client.getId(), code.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La oferta ya fue activada por este cliente");
        }

        boolean wasVip = Boolean.TRUE.equals(client.getVip());
        int currentCount = client.getWeeklyProductCount() == null ? 0 : client.getWeeklyProductCount();
        client.setWeeklyProductCount(currentCount + productQuantity);

        int requiredProducts = clientService.toSummary(client).vipRequiredProducts();
        boolean vipUnlocked = !wasVip && client.getWeeklyProductCount() >= requiredProducts;
        if (vipUnlocked) {
            clientService.updateVipStatus(client, true);
        }

        CodeActivation activation = activationRepository.save(
            new CodeActivation(null, client, code, productQuantity, LocalDate.now())
        );
        Client savedClient = clientRepository.save(client);

        String message = vipUnlocked
            ? "El círculo VIP se abrió para este cliente."
            : "Oferta activada y progreso actualizado.";

        return new OfferActivationResponse(
            toActivationResponse(activation),
            clientService.toSummary(savedClient),
            vipUnlocked,
            message
        );
    }

    public List<CodeActivationResponse> getActivationsForClient(Long clientId) {
        return activationRepository.findByClientIdOrderByActivatedAtDesc(clientId).stream()
            .map(this::toActivationResponse)
            .toList();
    }

    //frond-end screen
    public List<Code> getCodes(){
        return repository.findAll();
    }

    private CodeActivationResponse toActivationResponse(CodeActivation activation) {
        Code code = activation.getCode();
        return new CodeActivationResponse(
            activation.getId(),
            code.getId(),
            code.getTitle(),
            activation.getProductQuantity(),
            activation.getActivatedAt()
        );
    }

}
