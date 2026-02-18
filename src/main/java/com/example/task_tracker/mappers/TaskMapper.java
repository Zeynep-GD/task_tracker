package com.example.task_tracker.mappers;

import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.domain.entities.Task;

public interface TaskMapper {
    Task fromDto(TaskDto taskDto);
    TaskDto toDto(Task task);
}
