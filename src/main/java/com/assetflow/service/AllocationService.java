package com.assetflow.service;

import com.assetflow.dto.AllocationDTO.AllocateRequest;
import com.assetflow.dto.AllocationDTO.AllocationResponse;
import com.assetflow.dto.AllocationDTO.ReturnRequest;
import com.assetflow.entity.*;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AllocationRepository;
import com.assetflow.repository.AssetRepository;
import com.assetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AllocationService {

    private final AllocationRepository allocationRepository;
    private final AssetRepository assetRepository;
    private final UserRepository userRepository;

    /**
     * Core "no duplicate allocation" rule: an asset can only be allocated
     * if it is currently AVAILABLE and has no other ACTIVE allocation record.
     */
    @Transactional
    public AllocationResponse allocate(AllocateRequest request, String allocatingByEmail) {
        Asset asset = assetRepository.findById(request.getAssetId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found"));

        User allocatedTo = userRepository.findById(request.getAllocatedToId())
                .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

        User allocatedBy = userRepository.findByEmail(allocatingByEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Requesting user not found"));

        if (asset.getStatus() != AssetStatus.AVAILABLE) {
            throw new BusinessRuleException(
                    "Asset " + asset.getAssetCode() + " is not available (current status: "
                            + asset.getStatus() + "). Submit a Transfer Request instead.");
        }

        allocationRepository.findByAssetIdAndStatus(asset.getId(), AllocationStatus.ACTIVE)
                .ifPresent(a -> {
                    throw new BusinessRuleException("Asset already has an active allocation");
                });

        Allocation allocation = new Allocation();
        allocation.setAsset(asset);
        allocation.setAllocatedTo(allocatedTo);
        allocation.setAllocatedBy(allocatedBy);
        allocation.setAllocatedAt(LocalDateTime.now());
        allocation.setExpectedReturnDate(request.getExpectedReturnDate());
        allocation.setRemarks(request.getRemarks());
        allocation.setStatus(AllocationStatus.ACTIVE);

        asset.setStatus(AssetStatus.ALLOCATED);
        assetRepository.save(asset);

        return toResponse(allocationRepository.save(allocation));
    }

    @Transactional
    public AllocationResponse returnAsset(Long allocationId, ReturnRequest request) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation not found"));

        if (allocation.getStatus() != AllocationStatus.ACTIVE
                && allocation.getStatus() != AllocationStatus.OVERDUE) {
            throw new BusinessRuleException("This allocation is not currently active");
        }

        allocation.setReturnedAt(LocalDateTime.now());
        allocation.setReturnConditionRating(request.getReturnConditionRating());
        allocation.setRemarks(request.getRemarks());
        allocation.setStatus(AllocationStatus.RETURNED);

        Asset asset = allocation.getAsset();
        asset.setStatus(AssetStatus.AVAILABLE);
        if (request.getReturnConditionRating() != null) {
            asset.setConditionRating(request.getReturnConditionRating());
        }
        assetRepository.save(asset);

        return toResponse(allocationRepository.save(allocation));
    }

    public Page<AllocationResponse> listByUser(Long userId, Pageable pageable) {
        return allocationRepository.findByAllocatedToId(userId, pageable).map(this::toResponse);
    }

    public Page<AllocationResponse> listByAsset(Long assetId, Pageable pageable) {
        return allocationRepository.findByAssetId(assetId, pageable).map(this::toResponse);
    }

    private AllocationResponse toResponse(Allocation a) {
        AllocationResponse dto = new AllocationResponse();
        dto.setId(a.getId());
        dto.setAssetId(a.getAsset().getId());
        dto.setAssetCode(a.getAsset().getAssetCode());
        dto.setAssetName(a.getAsset().getName());
        dto.setAllocatedToId(a.getAllocatedTo().getId());
        dto.setAllocatedToName(a.getAllocatedTo().getFullName());
        if (a.getAllocatedBy() != null) {
            dto.setAllocatedById(a.getAllocatedBy().getId());
            dto.setAllocatedByName(a.getAllocatedBy().getFullName());
        }
        dto.setAllocatedAt(a.getAllocatedAt());
        dto.setExpectedReturnDate(a.getExpectedReturnDate());
        dto.setReturnedAt(a.getReturnedAt());
        dto.setStatus(a.getStatus().name());
        dto.setRemarks(a.getRemarks());
        return dto;
    }
}
