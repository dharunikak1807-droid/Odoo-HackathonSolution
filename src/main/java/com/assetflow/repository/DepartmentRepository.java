package com.assetflow.repository;

import com.assetflow.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    boolean existsByCode(String code);
}
