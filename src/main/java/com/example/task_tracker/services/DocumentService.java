package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.FileAttachmentDto;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.UUID;

public interface DocumentService {

    FileAttachmentDto uploadFileToTask(UUID taskId, MultipartFile file, UUID userId) throws IOException;

    FileAttachmentDto uploadFileToSpace(UUID spaceId, MultipartFile file, UUID userId) throws IOException;

    FileAttachmentDto uploadProfilePicture(UUID userId, MultipartFile file) throws IOException;

    byte[] downloadFile(UUID fileId);

    // Dosyanın tipini öğrenme
    String getContentType(UUID fileId);

    void deleteFile(UUID fileId, UUID userId);
}