package com.assetflow.service;

import com.assetflow.dto.AssetDTO;
import com.assetflow.entity.Asset;
import com.assetflow.entity.AssetCategory;
import com.assetflow.entity.AssetStatus;
import com.assetflow.entity.Department;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetCategoryRepository;
import com.assetflow.repository.AssetRepository;
import com.assetflow.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public AssetDTO create(AssetDTO dto) {
        AssetCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset category not found"));

        Asset asset = new Asset();
        asset.setAssetCode(generateAssetCode(category));
        applyDto(asset, dto, category);
        asset.setStatus(AssetStatus.AVAILABLE);
        asset.setHealthScore(100); // brand-new assets start at full health

        return toDto(assetRepository.save(asset));
    }

    @Transactional
    public AssetDTO update(Long id, AssetDTO dto) {
        Asset asset = findEntity(id);
        AssetCategory category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Asset category not found"));
        applyDto(asset, dto, category);
        return toDto(assetRepository.save(asset));
    }

    public AssetDTO getById(Long id) {
        return toDto(findEntity(id));
    }

    public Page<AssetDTO> list(Pageable pageable) {
        return assetRepository.findAll(pageable).map(this::toDto);
    }

    public Page<AssetDTO> search(String keyword, Pageable pageable) {
        return assetRepository.search(keyword, pageable).map(this::toDto);
    }

    public Page<AssetDTO> filterByStatus(AssetStatus status, Pageable pageable) {
        return assetRepository.findByStatus(status, pageable).map(this::toDto);
    }

    @Transactional
    public AssetDTO changeStatus(Long id, AssetStatus newStatus) {
        Asset asset = findEntity(id);
        asset.setStatus(newStatus);
        return toDto(assetRepository.save(asset));
    }

    private Asset findEntity(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found: " + id));
    }

    private String generateAssetCode(AssetCategory category) {
        String prefix = (category.getPrefix() == null || category.getPrefix().isBlank())
                ? "AST" : category.getPrefix();
        long sequence = assetRepository.countByCategoryId(category.getId()) + 1;
        String candidate;
        // Guard against rare collisions if assets were deleted/reused
        do {
            candidate = String.format("%s-%04d", prefix, sequence++);
        } while (assetRepository.findByAssetCode(candidate).isPresent());
        return candidate;
    }

    private void applyDto(Asset asset, AssetDTO dto, AssetCategory category) {
        asset.setName(dto.getName());
        asset.setCategory(category);
        asset.setSerialNumber(dto.getSerialNumber());
        asset.setPurchaseDate(dto.getPurchaseDate());
        asset.setPurchaseCost(dto.getPurchaseCost());
        asset.setWarrantyExpiry(dto.getWarrantyExpiry());
        asset.setLocation(dto.getLocation());
        asset.setConditionRating(dto.getConditionRating());
        asset.setPhotoUrl(dto.getPhotoUrl());
        asset.setDocumentUrl(dto.getDocumentUrl());
        asset.setBookable(dto.isBookable());

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            asset.setDepartment(department);
        } else {
            asset.setDepartment(null);
        }

        if (dto.getStatus() != null) {
            try {
                asset.setStatus(AssetStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException ex) {
                throw new BusinessRuleException("Invalid asset status: " + dto.getStatus());
            }
        }
    }

    private AssetDTO toDto(Asset asset) {
        AssetDTO dto = new AssetDTO();
        dto.setId(asset.getId());
        dto.setAssetCode(asset.getAssetCode());
        dto.setName(asset.getName());
        dto.setCategoryId(asset.getCategory().getId());
        dto.setCategoryName(asset.getCategory().getName());
        dto.setSerialNumber(asset.getSerialNumber());
        dto.setPurchaseDate(asset.getPurchaseDate());
        dto.setPurchaseCost(asset.getPurchaseCost());
        dto.setWarrantyExpiry(asset.getWarrantyExpiry());
        dto.setLocation(asset.getLocation());
        dto.setConditionRating(asset.getConditionRating());
        dto.setPhotoUrl(asset.getPhotoUrl());
        dto.setDocumentUrl(asset.getDocumentUrl());
        dto.setStatus(asset.getStatus().name());
        dto.setBookable(asset.isBookable());
        if (asset.getDepartment() != null) {
            dto.setDepartmentId(asset.getDepartment().getId());
            dto.setDepartmentName(asset.getDepartment().getName());
        }
        dto.setHealthScore(asset.getHealthScore());
        dto.setQrCodeUrl(asset.getQrCodeUrl());
        return dto;
    }
}
