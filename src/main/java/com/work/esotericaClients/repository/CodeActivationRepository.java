package com.work.esotericaClients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.work.esotericaClients.domain.entity.CodeActivation;

public interface CodeActivationRepository extends JpaRepository<CodeActivation, Long> {
    List<CodeActivation> findByClientIdOrderByActivatedAtDesc(Long clientId);
    long countByClientId(Long clientId);
    boolean existsByClientIdAndCodeId(Long clientId, Long codeId);
}
