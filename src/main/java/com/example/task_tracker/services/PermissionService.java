package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.PermissionRequest;
import com.example.task_tracker.domain.entities.PermissionType;
import java.util.UUID;

public interface PermissionService {

    void grantPermission(UUID spaceId, PermissionRequest request, UUID currentUser);

    void validateAccess(UUID spaceId, UUID userId, PermissionType requiredType);

    void grantTaskPermission(UUID taskId, PermissionRequest request, UUID currentUser);

    void validateTaskAccess(UUID taskId, UUID userId, PermissionType requiredType);
}