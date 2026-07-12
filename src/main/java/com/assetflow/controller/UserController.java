package com.assetflow.controller;

import com.assetflow.dto.RoleUpdateRequest;
import com.assetflow.entity.User;
import com.assetflow.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Employee directory and role management (Admin only for writes)")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','ASSET_MANAGER','DEPARTMENT_HEAD')")
    public ResponseEntity<Page<User>> list(Pageable pageable) {
        return ResponseEntity.ok(userService.listUsers(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    /**
     * Only ADMIN can promote/demote a user between EMPLOYEE, DEPARTMENT_HEAD, ASSET_MANAGER, ADMIN.
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateRole(@PathVariable Long id, @Valid @RequestBody RoleUpdateRequest request) {
        return ResponseEntity.ok(userService.updateRole(id, request.getRole()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> setEnabled(@PathVariable Long id, @RequestParam boolean enabled) {
        return ResponseEntity.ok(userService.setEnabled(id, enabled));
    }
}
