package com.example.task_tracker.controller;

import com.example.task_tracker.domain.dto.FileAttachmentDto;
import com.example.task_tracker.services.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/docs")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    //Task'a Dosya Yükleme
    @PostMapping(value = "/tasks/{taskId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileAttachmentDto> uploadToTask(
            @PathVariable UUID taskId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) throws IOException {
        return ResponseEntity.ok(documentService.uploadFileToTask(taskId, file, userId));
    }

    //Space'e Dosya Yükleme
    @PostMapping(value = "/spaces/{spaceId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileAttachmentDto> uploadToSpace(
            @PathVariable UUID spaceId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) throws IOException {
        return ResponseEntity.ok(documentService.uploadFileToSpace(spaceId, file, userId));
    }

    //Kullanıcı Profil Fotoğrafı Yükleme
    @PostMapping(value = "/users/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileAttachmentDto> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) throws IOException {
        return ResponseEntity.ok(documentService.uploadProfilePicture(userId, file));
    }

    //Dosya İndirme
    @GetMapping("/download/{fileId}")
    public ResponseEntity<byte[]> downloadFile(@PathVariable UUID fileId) {
        byte[] data = documentService.downloadFile(fileId);
        String contentType = documentService.getContentType(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileId + "\"")
                .body(data);
    }

    // Resim Gösterme
    @GetMapping("/view/{fileId}")
    public ResponseEntity<byte[]> viewFile(@PathVariable UUID fileId) {
        byte[] data = documentService.downloadFile(fileId);
        String contentType = documentService.getContentType(fileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileId + "\"")
                .body(data);
    }

    //Dosya Silme
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable UUID fileId,
            @AuthenticationPrincipal(expression = "id") UUID userId
    ) {
        documentService.deleteFile(fileId, userId);
        return ResponseEntity.noContent().build();
    }
}