package com.example.task_tracker.repositories;

import com.example.task_tracker.domain.entities.Task;
import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findBySpaceId(UUID spaceId);
    Optional<Task> findBySpaceIdAndId(UUID spaceId, UUID id);

    @Query(value = "SELECT * FROM tasks t WHERE ST_DWithin(t.location, :userLocation, :distance)", nativeQuery = true)
    List<Task> findNearbyTasks(@Param("userLocation") Point userLocation, @Param("distance") double distance);

    @Query(value = "SELECT DISTINCT t.* FROM tasks t " +
            "JOIN spaces s ON t.space_id = s.id " +
            "LEFT JOIN space_permissions p ON s.id = p.space_id " +
            "WHERE (s.owner_id = :userId OR p.user_id = :userId) " +
            "AND ST_DWithin(t.location, :userLocation, :distance, true)",
            nativeQuery = true)
    List<Task> findNearbyTasksAccessibleToUser(@Param("userId") UUID userId,
                                               @Param("userLocation") Point userLocation,
                                               @Param("distance") double distance);
}