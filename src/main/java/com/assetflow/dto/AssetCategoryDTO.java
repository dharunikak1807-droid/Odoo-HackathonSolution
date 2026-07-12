package com.assetflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AssetCategoryDTO {
    private Long id;

    @NotBlank(message = "Category name is required")
    private String name;

    private String description;
    private String prefix;
    private boolean active;
}
