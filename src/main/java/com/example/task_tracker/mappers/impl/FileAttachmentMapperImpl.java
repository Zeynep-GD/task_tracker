package com.example.task_tracker.mappers.impl;

import com.example.task_tracker.domain.dto.FileAttachmentDto;
import com.example.task_tracker.domain.entities.FileAttachment;
import com.example.task_tracker.mappers.FileAttachmentMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class FileAttachmentMapperImpl implements FileAttachmentMapper {

    @Override
    public FileAttachmentDto toDto(FileAttachment attachment) {
        if (attachment == null) {
            return null;
        }

        // Dosyayı indirmek için dinamik  URL oluşturma
        String downloadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/docs/download/")
                .path(attachment.getId().toString())
                .toUriString();

        return new FileAttachmentDto(
                attachment.getId(),
                attachment.getFileName(),
                attachment.getFileType(),
                downloadUrl
        );
    }
}