package com.work.esotericaClients.dto.menu;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuItemRequest {

    @NotBlank
    @Size(min = 3, max = 150)
    private String name;

    @NotBlank
    @Size(min = 3, max = 500)
    private String description;

    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    @Positive
    private Integer stock;

    @Size(max = 500)
    private String imageUrl;

    @NotNull
    private Boolean isAvailable;

    @NotNull
    private Long categoryId;
}

