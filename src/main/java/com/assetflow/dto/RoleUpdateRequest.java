package com.assetflow.dto;

import com.assetflow.entity.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RoleUpdateRequest {
    @NotNull(message = "Role is required")
    private Role role;
}
