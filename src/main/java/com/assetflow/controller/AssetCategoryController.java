package com.assetflow.controller;

import com.assetflow.dto.AssetCategoryDTO;
import com.assetflow.service.AssetCategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asset-categories")
@RequiredArgsConstructor
@Tag(name = "Asset Categories", description = "Organization setup - asset category management")
public class AssetCategoryController {

    private final AssetCategoryService categoryService;

    @GetMapping
    public ResponseEntity<Page<AssetCategoryDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(categoryService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetCategoryDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetCategoryDTO> create(@Valid @RequestBody AssetCategoryDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetCategoryDTO> update(@PathVariable Long id, @Valid @RequestBody AssetCategoryDTO dto) {
        return ResponseEntity.ok(categoryService.update(id, dto));
    }
}
