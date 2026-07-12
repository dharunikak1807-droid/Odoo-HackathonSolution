package com.assetflow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class PasswordDtos {

    @Data
    public static class ForgotPasswordRequest {
        @NotBlank
        @Email
        private String email;
    }

    @Data
    public static class ResetPasswordRequest {
        @NotBlank
        private String resetToken;

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }

    @Data
    public static class ChangePasswordRequest {
        @NotBlank
        private String currentPassword;

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String newPassword;
    }
}
