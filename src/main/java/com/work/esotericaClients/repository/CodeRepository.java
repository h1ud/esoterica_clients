package com.work.esotericaClients.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.work.esotericaClients.domain.entity.Code;

public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findByTitle(String title);
    List<Code> findByCreatedAt(LocalDate createdAt);
    List<Code> findByExpiration(LocalDate expiration);

}
