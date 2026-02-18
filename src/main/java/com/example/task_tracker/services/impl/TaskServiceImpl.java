package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.domain.entities.*;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.TaskMapper;
import com.example.task_tracker.repositories.SpaceRepository;
import com.example.task_tracker.repositories.TaskRepository;
import com.example.task_tracker.services.PermissionService;
import com.example.task_tracker.services.TaskService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SpaceRepository spaceRepository;
    private final TaskMapper taskMapper;
    private final PermissionService permissionService;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    public List<TaskDto> listTasks(UUID spaceId, UUID userId) {

        permissionService.validateAccess(spaceId, userId, PermissionType.VIEWER);

        return taskRepository.findBySpaceId(spaceId).stream()
                .map(taskMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public TaskDto createTask(UUID spaceId, TaskDto taskDto, UUID userId) {
        permissionService.validateAccess(spaceId, userId, PermissionType.EDITOR);

        Space space = spaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResourceNotFoundException("Görev eklenecek Alan bulunamadı! ID: " + spaceId));

        Task task = taskMapper.fromDto(taskDto);
        task.setSpace(space);
        task.setCreated(LocalDateTime.now());
        task.setUpdated(LocalDateTime.now());

        if (task.getStatus() == null) task.setStatus(TaskStatus.OPEN);
        if (task.getPriority() == null) task.setPriority(TaskPriority.MEDIUM);

        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Cacheable(value = "tasks", key = "#taskId")
    public TaskDto getTask(UUID spaceId, UUID taskId, UUID userId) {
        permissionService.validateTaskAccess(taskId, userId, PermissionType.VIEWER);

        return taskRepository.findBySpaceIdAndId(spaceId, taskId)
                .map(taskMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Görev bulunamadı!"));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public TaskDto updateTask(UUID spaceId, UUID taskId, TaskDto taskDto, UUID userId) {
        permissionService.validateTaskAccess(taskId, userId, PermissionType.EDITOR);

        Task task = taskRepository.findBySpaceIdAndId(spaceId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek Görev bulunamadı!"));

        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setPriority(taskDto.priority());
        task.setStatus(taskDto.status());
        task.setDueDate(taskDto.dueDate());

        if (taskDto.location() != null) {
            task.setLocation(geometryFactory.createPoint(new Coordinate(
                    taskDto.location().longitude(),
                    taskDto.location().latitude()
            )));
        }

        task.setUpdated(LocalDateTime.now());
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public TaskDto patchTask(UUID spaceId, UUID taskId, TaskDto taskDto, UUID userId) {
        permissionService.validateTaskAccess(taskId, userId, PermissionType.EDITOR);

        Task task = taskRepository.findBySpaceIdAndId(spaceId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Güncellenecek Görev bulunamadı!"));

        if (taskDto.title() != null) task.setTitle(taskDto.title());
        if (taskDto.description() != null) task.setDescription(taskDto.description());
        if (taskDto.priority() != null) task.setPriority(taskDto.priority());
        if (taskDto.status() != null) task.setStatus(taskDto.status());
        if (taskDto.dueDate() != null) task.setDueDate(taskDto.dueDate());

        if (taskDto.location() != null) {
            task.setLocation(geometryFactory.createPoint(new Coordinate(
                    taskDto.location().longitude(),
                    taskDto.location().latitude()
            )));
        }

        task.setUpdated(LocalDateTime.now());
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Override
    @Transactional
    @CacheEvict(value = "tasks", key = "#taskId")
    public void deleteTask(UUID spaceId, UUID taskId, UUID userId) {
        permissionService.validateTaskAccess(taskId, userId, PermissionType.EDITOR);

        Task task = taskRepository.findBySpaceIdAndId(spaceId, taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Silinecek Görev bulunamadı!"));

        taskRepository.delete(task);
    }

    @Override
    public List<TaskDto> getNearbyTasks(double lat, double lon, double radius, UUID userId) {
        Point userLocation = geometryFactory.createPoint(new Coordinate(lon, lat));
        return taskRepository.findNearbyTasksAccessibleToUser(userId, userLocation, radius)
                .stream()
                .map(taskMapper::toDto)
                .toList();
    }
}