package com.example.task_tracker.domain.dto;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;
public record UserDto(
        UUID id,
        String name,
        String email,
        List<SpaceDto> spaces,
        FileAttachmentDto profilePicture
) implements Serializable {
    private static final long serialVersionUID = 1L;
}


