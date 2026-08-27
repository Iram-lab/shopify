package com.ecommerce.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {

    public record RegisterRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8) String password,
        @NotBlank String firstName,
        @NotBlank String lastName
    ) {}

    public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank String password
    ) {}

    public record RefreshTokenRequest(
        @NotBlank String refreshToken
    ) {}

    public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        String email,
        String role
    ) {
        public static AuthResponse of(String accessToken, String refreshToken, String email, String role) {
            return new AuthResponse(accessToken, refreshToken, "Bearer", email, role);
        }
    }

    public record UserResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        String role
    ) {}
}
