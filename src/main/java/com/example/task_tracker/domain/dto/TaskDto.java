package com.example.task_tracker.domain.dto;

import com.example.task_tracker.domain.entities.TaskPriority;
import com.example.task_tracker.domain.entities.TaskStatus;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record TaskDto(
        UUID id,
        @NotBlank(message = "Görev başlığı zorunludur")
        String title,
        String description,
        LocalDateTime dueDate,
        TaskPriority priority,
        TaskStatus status,
        UUID spaceId,
        LocationDto location,
        List<FileAttachmentDto> attachments
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
