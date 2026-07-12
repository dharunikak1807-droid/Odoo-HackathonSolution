package com.assetflow.repository;

import com.assetflow.entity.AssetCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetCategoryRepository extends JpaRepository<AssetCategory, Long> {
    boolean existsByName(String name);
}
