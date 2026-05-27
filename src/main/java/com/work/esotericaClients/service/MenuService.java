package com.work.esotericaClients.service;

import com.work.esotericaClients.dto.menu.MenuCategoryResponse;
import com.work.esotericaClients.dto.menu.MenuItemRequest;
import com.work.esotericaClients.dto.menu.MenuItemResponse;
import com.work.esotericaClients.entity.MenuCategory;
import com.work.esotericaClients.entity.MenuItem;
import com.work.esotericaClients.exception.ResourceNotFoundException;
import com.work.esotericaClients.repository.MenuCategoryRepository;
import com.work.esotericaClients.repository.MenuItemRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuService {

    private final MenuCategoryRepository categoryRepository;
    private final MenuItemRepository itemRepository;

    public MenuService(MenuCategoryRepository categoryRepository, MenuItemRepository itemRepository) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> getMenu() {
        return categoryRepository.findAll().stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemResponse createMenuItem(MenuItemRequest request) {
        MenuCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .imageUrl(request.getImageUrl())
                .isAvailable(request.getIsAvailable())
                .category(category)
                .build();

        MenuItem saved = itemRepository.save(item);
        return toItemResponse(saved);
    }

    private MenuCategoryResponse toCategoryResponse(MenuCategory category) {
        return MenuCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .items(category.getItems().stream().map(this::toItemResponse).collect(Collectors.toList()))
                .build();
    }

    private MenuItemResponse toItemResponse(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stock(item.getStock())
                .imageUrl(item.getImageUrl())
                .isAvailable(item.getIsAvailable())
                .categoryId(item.getCategory().getId())
                .categoryName(item.getCategory().getName())
                .build();
    }
}

