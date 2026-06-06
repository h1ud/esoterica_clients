package com.work.esotericaClients.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.work.esotericaClients.auth.AdminAuthorizationService;
import com.work.esotericaClients.domain.entity.MenuItem;
import com.work.esotericaClients.service.MenuItemService;

@RestController
@RequestMapping("/api/menu")
public class MenuItemController {

    private final MenuItemService service;
    private final AdminAuthorizationService adminAuthorizationService;

    public MenuItemController(
        MenuItemService service,
        AdminAuthorizationService adminAuthorizationService
    ) {
        this.service = service;
        this.adminAuthorizationService = adminAuthorizationService;
    }

    @GetMapping
    public List<MenuItem> getAvailableMenuItems() {
        return service.getAvailableMenuItems();
    }

    @GetMapping("/admin")
    public List<MenuItem> getMenuItems(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.getMenuItems();
    }

    @GetMapping("/category/{category}")
    public List<MenuItem> getByCategory(@PathVariable String category) {
        return service.getByCategory(category);
    }

    @PostMapping
    public MenuItem createMenuItem(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody MenuItem menuItem
    ) {
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.createMenuItem(menuItem);
    }

    @PutMapping("/{id}")
    public MenuItem updateMenuItem(
        @PathVariable Long id,
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @RequestBody MenuItem menuItem
    ) {
        adminAuthorizationService.requireAdmin(authorizationHeader);
        return service.updateMenuItem(id, menuItem);
    }

    @DeleteMapping("/{id}")
    public void deleteMenuItem(
        @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
        @PathVariable Long id
    ) {
        adminAuthorizationService.requireAdmin(authorizationHeader);
        service.deleteMenuItem(id);
    }
}
