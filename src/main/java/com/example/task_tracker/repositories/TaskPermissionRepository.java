package com.example.task_tracker.repositories;

import com.example.task_tracker.domain.entities.TaskPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskPermissionRepository extends JpaRepository<TaskPermission, UUID> {
    // Yetki Kontrolü
    Optional<TaskPermission> findByUserIdAndTaskId(UUID userId, UUID taskId);
}