package com.work.esotericaClients.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.work.esotericaClients.domain.entity.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByCategoryIgnoreCase(String category);
    List<MenuItem> findByIsAvailableTrueOrderByIsFeaturedDescTitleAsc();
}
