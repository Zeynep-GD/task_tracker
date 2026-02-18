package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.FileAttachmentDto;
import com.example.task_tracker.domain.entities.*;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.FileAttachmentMapper;
import com.example.task_tracker.repositories.FileAttachmentRepository;
import com.example.task_tracker.repositories.SpaceRepository;
import com.example.task_tracker.repositories.TaskRepository;
import com.example.task_tracker.repositories.UserRepository;
import com.example.task_tracker.services.DocumentService;
import com.example.task_tracker.services.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final FileAttachmentRepository fileRepository;
    private final TaskRepository taskRepository;
    private final SpaceRepository spaceRepository;
    private final UserRepository userRepository;
    private final PermissionService permissionService;
    private final FileAttachmentMapper fileMapper;

    @Override
    @Transactional
    public FileAttachmentDto uploadFileToTask(UUID taskId, MultipartFile file, UUID userId) throws IOException {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task bulunamadı"));

        permissionService.validateTaskAccess(taskId, userId, PermissionType.EDITOR);

        FileAttachment attachment = createFileAttachment(file);
        attachment.setTask(task);

        return fileMapper.toDto(fileRepository.save(attachment));
    }

    @Override
    @Transactional
    public FileAttachmentDto uploadFileToSpace(UUID spaceId, MultipartFile file, UUID userId) throws IOException {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space bulunamadı"));
        permissionService.validateAccess(spaceId, userId, PermissionType.EDITOR);

        FileAttachment attachment = createFileAttachment(file);
        attachment.setSpace(space);

        return fileMapper.toDto(fileRepository.save(attachment));
    }

    @Override
    @Transactional
    public FileAttachmentDto uploadProfilePicture(UUID userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        if (user.getProfilePicture() != null) {
            fileRepository.delete(user.getProfilePicture());
        }

        FileAttachment attachment = createFileAttachment(file);
        FileAttachment savedAttachment = fileRepository.save(attachment);

        user.setProfilePicture(savedAttachment);
        userRepository.save(user);

        return fileMapper.toDto(savedAttachment);
    }

    @Override
    public byte[] downloadFile(UUID fileId) {
        FileAttachment attachment = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Dosya bulunamadı"));
        return attachment.getData();
    }

    @Override
    public String getContentType(UUID fileId) {
        return fileRepository.findById(fileId)
                .map(FileAttachment::getFileType)
                .orElse("application/octet-stream");
    }

    @Override
    @Transactional
    public void deleteFile(UUID fileId, UUID userId) {
        FileAttachment attachment = fileRepository.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("Dosya bulunamadı"));

        if (attachment.getTask() != null) {
            permissionService.validateTaskAccess(attachment.getTask().getId(), userId, PermissionType.EDITOR);
        }
        else if (attachment.getSpace() != null) {
            permissionService.validateAccess(attachment.getSpace().getId(), userId, PermissionType.EDITOR);
        }
        else {
            User currentUser = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

            if (currentUser.getProfilePicture() != null &&
                    currentUser.getProfilePicture().getId().equals(fileId)) {

                currentUser.setProfilePicture(null);
                userRepository.save(currentUser);

            } else {
                throw new AccessDeniedException("Bu dosyayı silmeye yetkiniz yok!");
            }
        }

        fileRepository.delete(attachment);
    }

    private FileAttachment createFileAttachment(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        if(fileName.contains("..")) {
            throw new RuntimeException("Dosya adı geçersiz karakterler içeriyor: " + fileName);
        }
        return FileAttachment.builder()
                .fileName(fileName)
                .fileType(file.getContentType())
                .data(file.getBytes())
                .build();
    }
}