package com.assetflow.service;

import com.assetflow.dto.AssetCategoryDTO;
import com.assetflow.entity.AssetCategory;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.AssetCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetCategoryService {

    private final AssetCategoryRepository categoryRepository;

    @Transactional
    public AssetCategoryDTO create(AssetCategoryDTO dto) {
        if (categoryRepository.existsByName(dto.getName())) {
            throw new BusinessRuleException("A category with this name already exists");
        }
        AssetCategory category = new AssetCategory();
        applyDto(category, dto);
        return toDto(categoryRepository.save(category));
    }

    @Transactional
    public AssetCategoryDTO update(Long id, AssetCategoryDTO dto) {
        AssetCategory category = findEntity(id);
        applyDto(category, dto);
        return toDto(categoryRepository.save(category));
    }

    public AssetCategoryDTO getById(Long id) {
        return toDto(findEntity(id));
    }

    public Page<AssetCategoryDTO> list(Pageable pageable) {
        return categoryRepository.findAll(pageable).map(this::toDto);
    }

    private AssetCategory findEntity(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asset category not found: " + id));
    }

    private void applyDto(AssetCategory category, AssetCategoryDTO dto) {
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setPrefix(dto.getPrefix() == null || dto.getPrefix().isBlank()
                ? "AST" : dto.getPrefix().toUpperCase());
        category.setActive(dto.isActive());
    }

    private AssetCategoryDTO toDto(AssetCategory category) {
        AssetCategoryDTO dto = new AssetCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setPrefix(category.getPrefix());
        dto.setActive(category.isActive());
        return dto;
    }
}
