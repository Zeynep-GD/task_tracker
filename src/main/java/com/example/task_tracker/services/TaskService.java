package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.TaskDto;

import java.util.List;
import java.util.UUID;

public interface TaskService {
    List<TaskDto> listTasks(UUID spaceId, UUID userId);
    TaskDto createTask(UUID spaceId, TaskDto taskDto, UUID userId);
    TaskDto getTask(UUID spaceId, UUID taskId, UUID userId);
    TaskDto updateTask(UUID spaceId, UUID taskId, TaskDto taskDto, UUID userId);
    TaskDto patchTask(UUID spaceId, UUID taskId, TaskDto taskDto, UUID userId);
    void deleteTask(UUID spaceId, UUID taskId, UUID userId);
    List<TaskDto> getNearbyTasks(double lat, double lon, double radius, UUID userId);
}