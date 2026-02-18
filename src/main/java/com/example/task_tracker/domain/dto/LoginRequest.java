package com.example.task_tracker.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "E-posta girilmelidir")
        @Email(message = "Format hatalı")
        String email,

        @NotBlank(message = "Şifre girilmelidir")
        String password
) {}

