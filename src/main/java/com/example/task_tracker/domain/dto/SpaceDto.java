package com.example.task_tracker.domain.dto;

import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public record SpaceDto(
        UUID id,
        @NotBlank(message = "Alan başlığı zorunludur")
        String title,
        String description,
        UUID ownerId,
        List<TaskDto> tasks,
        LocationDto centerLocation,
        List<FileAttachmentDto> attachments
) implements Serializable {
    private static final long serialVersionUID = 1L;
}

