package com.example.task_tracker.domain.dto;

import com.example.task_tracker.domain.entities.PermissionType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PermissionRequest(
        @NotBlank @Email @NotNull(message = "Kullanıcı seçilmelidir")
        String email,// UUID yerine artık e-posta adresi alıyoruz

        @NotNull(message = "Yetki türü seçilmelidir")
        PermissionType type
) {}