package com.example.task_tracker.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "İsim alanı boş bırakılamaz")
        String name,

        @NotBlank(message = "E-posta boş bırakılamaz")
        @Email(message = "Geçerli bir e-posta adresi giriniz")
        String email,

        @NotBlank(message = "Şifre boş bırakılamaz")
        @Size(min = 6, message = "Şifre en az 6 karakter olmalıdır")
        String password
) {}
