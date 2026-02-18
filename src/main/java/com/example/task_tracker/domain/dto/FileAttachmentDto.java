package com.example.task_tracker.domain.dto;

import java.io.Serializable;
import java.util.UUID;

public record FileAttachmentDto(
        UUID id,
        String fileName,
        String fileType,
        String downloadUrl
) implements Serializable {
    private static final long serialVersionUID = 1L;
}
