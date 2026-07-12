package com.assetflow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "departments")
public class Department extends BaseEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 20, unique = true)
    private String code;

    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_department_id")
    private Department parentDepartment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "head_user_id")
    private User departmentHead;

    @Column(nullable = false)
    private boolean active = true;
}
