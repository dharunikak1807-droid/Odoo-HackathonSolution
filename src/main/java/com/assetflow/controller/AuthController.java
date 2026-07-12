package com.assetflow.controller;

import com.assetflow.dto.JwtResponse;
import com.assetflow.dto.LoginRequest;
import com.assetflow.dto.PasswordDtos.ChangePasswordRequest;
import com.assetflow.dto.PasswordDtos.ForgotPasswordRequest;
import com.assetflow.dto.PasswordDtos.ResetPasswordRequest;
import com.assetflow.dto.RegisterRequest;
import com.assetflow.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register, login, and password management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok("If an account exists for this email, a reset link has been sent.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok("Password has been reset successfully.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                                   Authentication authentication) {
        authService.changePassword(authentication.getName(), request);
        return ResponseEntity.ok("Password changed successfully.");
    }
}
