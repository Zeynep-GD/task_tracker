package com.example.task_tracker.mappers;

import com.example.task_tracker.domain.dto.FileAttachmentDto;
import com.example.task_tracker.domain.entities.FileAttachment;

public interface FileAttachmentMapper {
    FileAttachmentDto toDto(FileAttachment attachment);
}