package com.assetflow.service;

import com.assetflow.dto.DepartmentDTO;
import com.assetflow.entity.Department;
import com.assetflow.entity.User;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.DepartmentRepository;
import com.assetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    @Transactional
    public DepartmentDTO create(DepartmentDTO dto) {
        if (departmentRepository.existsByName(dto.getName())) {
            throw new BusinessRuleException("A department with this name already exists");
        }

        Department department = new Department();
        applyDto(department, dto);
        return toDto(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentDTO update(Long id, DepartmentDTO dto) {
        Department department = findEntity(id);
        applyDto(department, dto);
        return toDto(departmentRepository.save(department));
    }

    public DepartmentDTO getById(Long id) {
        return toDto(findEntity(id));
    }

    public Page<DepartmentDTO> list(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(this::toDto);
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        Department department = findEntity(id);
        department.setActive(active);
        departmentRepository.save(department);
    }

    private Department findEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found: " + id));
    }

    private void applyDto(Department department, DepartmentDTO dto) {
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setDescription(dto.getDescription());

        if (dto.getParentDepartmentId() != null) {
            Department parent = departmentRepository.findById(dto.getParentDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent department not found"));
            department.setParentDepartment(parent);
        } else {
            department.setParentDepartment(null);
        }

        if (dto.getDepartmentHeadId() != null) {
            User head = userRepository.findById(dto.getDepartmentHeadId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department head user not found"));
            department.setDepartmentHead(head);
        } else {
            department.setDepartmentHead(null);
        }
    }

    private DepartmentDTO toDto(Department department) {
        DepartmentDTO dto = new DepartmentDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setCode(department.getCode());
        dto.setDescription(department.getDescription());
        dto.setActive(department.isActive());
        if (department.getParentDepartment() != null) {
            dto.setParentDepartmentId(department.getParentDepartment().getId());
        }
        if (department.getDepartmentHead() != null) {
            dto.setDepartmentHeadId(department.getDepartmentHead().getId());
            dto.setDepartmentHeadName(department.getDepartmentHead().getFullName());
        }
        return dto;
    }
}
