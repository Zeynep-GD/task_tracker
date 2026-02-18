package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.PermissionRequest;
import com.example.task_tracker.domain.entities.*;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.repositories.*;
import com.example.task_tracker.services.PermissionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final SpacePermissionRepository spacePermissionRepository;
    private final TaskPermissionRepository taskPermissionRepository;
    private final SpaceRepository spaceRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void grantPermission(UUID spaceId, PermissionRequest request, UUID currentUser) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space bulunamadı"));

        boolean isActualOwner = space.getOwner().getId().equals(currentUser);
        boolean hasOwnerPermission = spacePermissionRepository
                .findByUserIdAndSpaceId(currentUser, spaceId)
                .map(p -> p.getPermissionType() == PermissionType.OWNER)
                .orElse(false);

        if (!isActualOwner && !hasOwnerPermission) {
            throw new AccessDeniedException("Yetki verme işlemi için OWNER yetkisine sahip olmalısınız!");
        }

        User grantee = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + request.email()));

        // Yetki kaydetme işlemleri
        SpacePermission permission = spacePermissionRepository.findByUserIdAndSpaceId(grantee.getId(), spaceId)
                .orElse(new SpacePermission());

        permission.setSpace(space);
        permission.setUser(grantee);
        permission.setPermissionType(request.type());
        spacePermissionRepository.save(permission);
    }

    @Override
    public void validateAccess(UUID spaceId, UUID userId, PermissionType requiredType) {
        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Space bulunamadı"));

        if (space.getOwner().getId().equals(userId)) {
            return;
        }

        SpacePermission permission = spacePermissionRepository.findByUserIdAndSpaceId(userId, spaceId)
                .orElseThrow(() -> new AccessDeniedException("Bu alana erişim yetkiniz yok!"));

        // Kullanıcının yetkisi istenen yetkiden küçükse hata ver
        if (permission.getPermissionType().ordinal() < requiredType.ordinal()) {
            throw new AccessDeniedException("Yetkiniz yetersiz! Gereken en az: " + requiredType);
        }
    }


    @Override
    @Transactional
    public void grantTaskPermission(UUID taskId, PermissionRequest request, UUID currentUser) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task bulunamadı"));

        boolean isSpaceOwner = task.getSpace().getOwner().getId().equals(currentUser);

        boolean hasSpaceOwnerPerm = spacePermissionRepository.findByUserIdAndSpaceId(currentUser, task.getSpace().getId())
                .map(p -> p.getPermissionType() == PermissionType.OWNER).orElse(false);

        if (!isSpaceOwner && !hasSpaceOwnerPerm) {
            throw new AccessDeniedException("Başkasına yetki vermek için bu alanın veya görevin yöneticisi olmalısınız.");
        }

        User grantee = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));

        TaskPermission permission = taskPermissionRepository.findByUserIdAndTaskId(grantee.getId(), taskId)
                .orElse(new TaskPermission());

        permission.setTask(task);
        permission.setUser(grantee);
        permission.setPermissionType(request.type());
        taskPermissionRepository.save(permission);
    }

    @Override
    public void validateTaskAccess(UUID taskId, UUID userId, PermissionType requiredType) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task bulunamadı"));

        UUID spaceId = task.getSpace().getId();

        if (task.getSpace().getOwner().getId().equals(userId)) {
            return;
        }

        var spacePerm = spacePermissionRepository.findByUserIdAndSpaceId(userId, spaceId);
        if (spacePerm.isPresent() && spacePerm.get().getPermissionType().ordinal() >= requiredType.ordinal()) {
            return;
        }

        var taskPerm = taskPermissionRepository.findByUserIdAndTaskId(userId, taskId);
        if (taskPerm.isPresent() && taskPerm.get().getPermissionType().ordinal() >= requiredType.ordinal()) {
            return;
        }

        throw new AccessDeniedException("Bu göreve erişim yetkiniz yetersiz! Gereken: " + requiredType);
    }
}
