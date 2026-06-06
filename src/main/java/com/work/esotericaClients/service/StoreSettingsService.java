package com.work.esotericaClients.service;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.work.esotericaClients.domain.entity.StoreSettings;
import com.work.esotericaClients.dto.VipSettingsResponse;
import com.work.esotericaClients.repository.StoreSettingsRepository;

@Service
public class StoreSettingsService {
    private static final Long SETTINGS_ID = 1L;
    private static final int DEFAULT_VIP_REQUIRED_PRODUCTS = 10;

    private final StoreSettingsRepository repository;

    public StoreSettingsService(StoreSettingsRepository repository) {
        this.repository = repository;
    }

    public StoreSettings getSettings() {
        return repository.findById(SETTINGS_ID)
            .orElseGet(() -> repository.save(
                new StoreSettings(SETTINGS_ID, DEFAULT_VIP_REQUIRED_PRODUCTS, LocalDate.now())
            ));
    }

    public VipSettingsResponse getVipSettings() {
        StoreSettings settings = getSettings();
        return new VipSettingsResponse(settings.getVipRequiredProducts(), settings.getUpdatedAt());
    }

    public VipSettingsResponse updateVipRequiredProducts(Integer requiredProducts) {
        if (requiredProducts == null || requiredProducts < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La meta VIP debe ser mayor a cero");
        }

        StoreSettings settings = getSettings();
        settings.setVipRequiredProducts(requiredProducts);
        settings.setUpdatedAt(LocalDate.now());
        StoreSettings saved = repository.save(settings);

        return new VipSettingsResponse(saved.getVipRequiredProducts(), saved.getUpdatedAt());
    }
}
