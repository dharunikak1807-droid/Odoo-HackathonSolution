package com.assetflow.service;

import com.assetflow.dto.JwtResponse;
import com.assetflow.dto.LoginRequest;
import com.assetflow.dto.PasswordDtos.ChangePasswordRequest;
import com.assetflow.dto.PasswordDtos.ForgotPasswordRequest;
import com.assetflow.dto.PasswordDtos.ResetPasswordRequest;
import com.assetflow.dto.RegisterRequest;
import com.assetflow.entity.Role;
import com.assetflow.entity.User;
import com.assetflow.exception.BusinessRuleException;
import com.assetflow.exception.ResourceNotFoundException;
import com.assetflow.repository.UserRepository;
import com.assetflow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Transactional
    public JwtResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessRuleException("An account with this email already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // Signup can NEVER create anything other than EMPLOYEE.
        user.setRole(Role.EMPLOYEE);
        user.setEnabled(true);

        User saved = userRepository.save(user);
        String token = jwtUtil.generateToken(saved);

        return toJwtResponse(saved, token);
    }

    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        String token = jwtUtil.generateToken(user);
        return toJwtResponse(user, token);
    }

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("No account found with this email"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        // In production: send `token` via email using an email service.
        // For the hackathon demo, the token is returned directly for testing convenience.
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByResetToken(request.getResetToken())
                .orElseThrow(() -> new BusinessRuleException("Invalid or expired reset token"));

        if (user.getResetTokenExpiry() == null || user.getResetTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Reset token has expired. Please request a new one.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetToken(null);
        user.setResetTokenExpiry(null);
        userRepository.save(user);
    }

    @Transactional
    public void changePassword(String currentUserEmail, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BusinessRuleException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private JwtResponse toJwtResponse(User user, String token) {
        return JwtResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
