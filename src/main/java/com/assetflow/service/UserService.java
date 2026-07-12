package com.assetflow.service;

import com.assetflow.entity.Role;
import com.assetflow.entity.User;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> listUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
    }

    /**
     * Only an ADMIN may call this (enforced at the controller with @PreAuthorize).
     * Promotes/demotes a user between EMPLOYEE, DEPARTMENT_HEAD, ASSET_MANAGER, ADMIN.
     */
    @Transactional
    public User updateRole(Long userId, Role newRole) {
        User user = getById(userId);

        if (user.getRole() == newRole) {
            throw new BusinessRuleException("User already has role " + newRole);
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    @Transactional
    public User setEnabled(Long userId, boolean enabled) {
        User user = getById(userId);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }
}
