package com.work.esotericaClients.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.work.esotericaClients.domain.entity.MenuItem;
import com.work.esotericaClients.repository.MenuItemRepository;

@Service
public class MenuItemService {

    private final MenuItemRepository repository;

    public MenuItemService(MenuItemRepository repository) {
        this.repository = repository;
    }

    public List<MenuItem> getMenuItems() {
        return repository.findAll();
    }

    public List<MenuItem> getAvailableMenuItems() {
        return repository.findByIsAvailableTrueOrderByIsFeaturedDescTitleAsc();
    }

    public List<MenuItem> getByCategory(String category) {
        return repository.findByCategoryIgnoreCase(category);
    }

    public MenuItem createMenuItem(MenuItem menuItem) {
        applyDefaults(menuItem);
        menuItem.setId(null);
        menuItem.setCreatedAt(LocalDate.now());

        return repository.save(menuItem);
    }

    public MenuItem updateMenuItem(Long id, MenuItem menuItem) {
        MenuItem existing = repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));

        existing.setTitle(menuItem.getTitle());
        existing.setDescription(menuItem.getDescription());
        existing.setCategory(menuItem.getCategory());
        existing.setPrice(menuItem.getPrice());
        existing.setImageUrl(menuItem.getImageUrl());
        existing.setIsAvailable(menuItem.getIsAvailable());
        existing.setIsFeatured(menuItem.getIsFeatured());
        applyDefaults(existing);

        return repository.save(existing);
    }

    public void deleteMenuItem(Long id) {
        repository.deleteById(id);
    }

    private void applyDefaults(MenuItem menuItem) {
        if (menuItem.getTitle() == null || menuItem.getTitle().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El producto necesita nombre");
        }

        if (menuItem.getCategory() == null || menuItem.getCategory().isBlank()) {
            menuItem.setCategory("Especiales");
        }

        if (menuItem.getPrice() == null || menuItem.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El precio debe ser positivo");
        }

        if (menuItem.getIsAvailable() == null) {
            menuItem.setIsAvailable(true);
        }

        if (menuItem.getIsFeatured() == null) {
            menuItem.setIsFeatured(false);
        }

        if (menuItem.getCreatedAt() == null) {
            menuItem.setCreatedAt(LocalDate.now());
        }
    }
}
