package com.assetflow.controller;

import com.assetflow.dto.AssetDTO;
import com.assetflow.entity.AssetStatus;
import com.assetflow.service.AssetService;
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
@RequestMapping("/api/assets")
@RequiredArgsConstructor
@Tag(name = "Assets", description = "Asset registration, search, filtering and lifecycle status")
public class AssetController {

    private final AssetService assetService;

    @GetMapping
    public ResponseEntity<Page<AssetDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(assetService.list(pageable));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<AssetDTO>> search(@RequestParam String keyword, Pageable pageable) {
        return ResponseEntity.ok(assetService.search(keyword, pageable));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<Page<AssetDTO>> filterByStatus(@PathVariable AssetStatus status, Pageable pageable) {
        return ResponseEntity.ok(assetService.filterByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AssetDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetDTO> create(@Valid @RequestBody AssetDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetDTO> update(@PathVariable Long id, @Valid @RequestBody AssetDTO dto) {
        return ResponseEntity.ok(assetService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER')")
    public ResponseEntity<AssetDTO> changeStatus(@PathVariable Long id, @RequestParam AssetStatus status) {
        return ResponseEntity.ok(assetService.changeStatus(id, status));
    }
}
