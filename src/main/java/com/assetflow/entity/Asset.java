package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String assetCode; // Auto-generated e.g. AF-0001

    @Column(nullable = false, length = 150)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private AssetCategory category;

    @Column(length = 100)
    private String serialNumber;

    private LocalDate purchaseDate;

    private BigDecimal purchaseCost;

    private LocalDate warrantyExpiry;

    @Column(length = 150)
    private String location;

    @Column(length = 30)
    private String conditionRating; // e.g. NEW, GOOD, FAIR, POOR

    private String photoUrl;

    private String documentUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AssetStatus status = AssetStatus.AVAILABLE;

    @Column(nullable = false)
    private boolean bookable = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    /** 0-100 computed asset health score (age, usage, repairs). */
    private Integer healthScore;

    private String qrCodeUrl;
}
