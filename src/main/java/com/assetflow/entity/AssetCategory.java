package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "asset_categories")
public class AssetCategory extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    private String description;

    @Column(length = 10)
    private String prefix; // e.g. "AF" used when auto-generating asset IDs

    @Column(nullable = false)
    private boolean active = true;
}
