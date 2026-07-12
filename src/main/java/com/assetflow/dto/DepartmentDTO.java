package com.assetflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DepartmentDTO {
    private Long id;

    @NotBlank(message = "Department name is required")
    private String name;

    private String code;
    private String description;
    private Long parentDepartmentId;
    private Long departmentHeadId;
    private String departmentHeadName;
    private boolean active;
}
