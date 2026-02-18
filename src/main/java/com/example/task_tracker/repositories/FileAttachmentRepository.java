package com.example.task_tracker.repositories;

import com.example.task_tracker.domain.entities.FileAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {

    // Belirli bir Task'a ait tüm dosyaları bulmak için
    List<FileAttachment> findAllByTaskId(UUID taskId);

    // Belirli bir Space'e ait tüm dosyaları bulmak için
    List<FileAttachment> findAllBySpaceId(UUID spaceId);
}