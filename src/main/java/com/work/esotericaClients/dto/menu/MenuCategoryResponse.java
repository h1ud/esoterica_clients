package com.work.esotericaClients.dto.menu;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MenuCategoryResponse {

    private Long id;
    private String name;
    private String description;
    private Boolean isActive;
    private List<MenuItemResponse> items;
}

