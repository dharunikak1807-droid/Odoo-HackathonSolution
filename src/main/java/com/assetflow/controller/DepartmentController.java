package com.assetflow.controller;

import com.assetflow.dto.DepartmentDTO;
import com.assetflow.service.DepartmentService;
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
@RequestMapping("/api/departments")
@RequiredArgsConstructor
@Tag(name = "Departments", description = "Organization setup - department management")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<Page<DepartmentDTO>> list(Pageable pageable) {
        return ResponseEntity.ok(departmentService.list(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> create(@Valid @RequestBody DepartmentDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DepartmentDTO> update(@PathVariable Long id, @Valid @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.update(id, dto));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> setActive(@PathVariable Long id, @RequestParam boolean active) {
        departmentService.setActive(id, active);
        return ResponseEntity.noContent().build();
    }
}
