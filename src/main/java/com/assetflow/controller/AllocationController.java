package com.assetflow.controller;

import com.assetflow.dto.AllocationDTO.AllocateRequest;
import com.assetflow.dto.AllocationDTO.AllocationResponse;
import com.assetflow.dto.AllocationDTO.ReturnRequest;
import com.assetflow.service.AllocationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/allocations")
@RequiredArgsConstructor
@Tag(name = "Allocations", description = "Asset allocation and return workflow with duplicate-allocation prevention")
public class AllocationController {

    private final AllocationService allocationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD')")
    public ResponseEntity<AllocationResponse> allocate(@RequestBody AllocateRequest request,
                                                         Authentication authentication) {
        AllocationResponse response = allocationService.allocate(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD')")
    public ResponseEntity<AllocationResponse> returnAsset(@PathVariable Long id,
                                                            @RequestBody ReturnRequest request) {
        return ResponseEntity.ok(allocationService.returnAsset(id, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<AllocationResponse>> listByUser(@PathVariable Long userId, Pageable pageable) {
        return ResponseEntity.ok(allocationService.listByUser(userId, pageable));
    }

    @GetMapping("/asset/{assetId}")
    public ResponseEntity<Page<AllocationResponse>> listByAsset(@PathVariable Long assetId, Pageable pageable) {
        return ResponseEntity.ok(allocationService.listByAsset(assetId, pageable));
    }
}
