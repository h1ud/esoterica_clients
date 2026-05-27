package com.work.esotericaClients.service;

import com.work.esotericaClients.dto.menu.MenuCategoryRequest;
import com.work.esotericaClients.dto.menu.MenuCategoryResponse;
import com.work.esotericaClients.entity.MenuCategory;
import com.work.esotericaClients.exception.ResourceNotFoundException;
import com.work.esotericaClients.repository.MenuCategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MenuCategoryService {

    private final MenuCategoryRepository categoryRepository;

    public MenuCategoryService(MenuCategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public MenuCategoryResponse create(MenuCategoryRequest request) {
        MenuCategory category = MenuCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .isActive(request.getIsActive())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Transactional(readOnly = true)
    public List<MenuCategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuCategoryResponse findById(Long id) {
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        return toResponse(category);
    }

    @Transactional
    public MenuCategoryResponse update(Long id, MenuCategoryRequest request) {
        MenuCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());

        return toResponse(categoryRepository.save(category));
    }

    private MenuCategoryResponse toResponse(MenuCategory category) {
        return MenuCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .items(null)
                .build();
    }
}

