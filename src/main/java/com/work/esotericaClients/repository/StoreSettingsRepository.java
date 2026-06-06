package com.work.esotericaClients.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.work.esotericaClients.domain.entity.StoreSettings;

public interface StoreSettingsRepository extends JpaRepository<StoreSettings, Long> {
}
