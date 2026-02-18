package com.example.task_tracker.repositories;

import com.example.task_tracker.domain.entities.PermissionType;
import com.example.task_tracker.domain.entities.SpacePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SpacePermissionRepository extends JpaRepository<SpacePermission, UUID> {

    // Yetki Kontrolü
    Optional<SpacePermission> findByUserIdAndSpaceId(UUID userId, UUID spaceId);

    boolean existsByUserIdAndSpaceIdAndPermissionType(UUID userId, UUID spaceId, PermissionType permissionType);
}