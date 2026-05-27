package com.work.esotericaClients.controller;

import com.work.esotericaClients.dto.menu.MenuCategoryResponse;
import com.work.esotericaClients.dto.menu.MenuItemRequest;
import com.work.esotericaClients.dto.menu.MenuItemResponse;
import com.work.esotericaClients.service.MenuService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping
    public ResponseEntity<List<MenuCategoryResponse>> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(menuService.createMenuItem(request));
    }
}

