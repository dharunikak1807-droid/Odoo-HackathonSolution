package com.assetflow.repository;

import com.assetflow.entity.Asset;
import com.assetflow.entity.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findByAssetCode(String assetCode);

    long countByCategoryId(Long categoryId);

    long countByStatus(AssetStatus status);

    Page<Asset> findByStatus(AssetStatus status, Pageable pageable);

    Page<Asset> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT a FROM Asset a WHERE " +
           "LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.assetCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(a.serialNumber) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Asset> search(String keyword, Pageable pageable);
}
