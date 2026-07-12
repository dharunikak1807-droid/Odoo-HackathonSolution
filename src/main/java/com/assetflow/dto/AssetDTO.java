package com.assetflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AssetDTO {
    private Long id;
    private String assetCode; // read-only, auto-generated

    @NotBlank(message = "Asset name is required")
    private String name;

    @NotNull(message = "Category is required")
    private Long categoryId;
    private String categoryName;

    private String serialNumber;
    private LocalDate purchaseDate;
    private BigDecimal purchaseCost;
    private LocalDate warrantyExpiry;
    private String location;
    private String conditionRating;
    private String photoUrl;
    private String documentUrl;
    private String status; // AVAILABLE, ALLOCATED, RESERVED, MAINTENANCE, LOST, RETIRED, DISPOSED
    private boolean bookable;
    private Long departmentId;
    private String departmentName;
    private Integer healthScore;
    private String qrCodeUrl;
}
