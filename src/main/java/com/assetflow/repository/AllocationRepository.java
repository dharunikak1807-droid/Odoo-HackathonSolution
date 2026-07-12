package com.assetflow.repository;

import com.assetflow.entity.Allocation;
import com.assetflow.entity.AllocationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    Optional<Allocation> findByAssetIdAndStatus(Long assetId, AllocationStatus status);

    Page<Allocation> findByAllocatedToId(Long userId, Pageable pageable);

    Page<Allocation> findByAssetId(Long assetId, Pageable pageable);

    long countByStatus(AllocationStatus status);
}
