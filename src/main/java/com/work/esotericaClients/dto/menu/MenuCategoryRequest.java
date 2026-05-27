package com.work.esotericaClients.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuCategoryRequest {

    @NotBlank
    @Size(min = 3, max = 120)
    private String name;

    @NotBlank
    @Size(min = 3, max = 500)
    private String description;

    @NotNull
    private Boolean isActive;
}

