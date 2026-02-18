package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.domain.entities.*;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.TaskMapper;
import com.example.task_tracker.repositories.SpaceRepository;
import com.example.task_tracker.repositories.TaskRepository;
import com.example.task_tracker.services.PermissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private SpaceRepository spaceRepository;
    @Mock
    private TaskMapper taskMapper;
    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private final UUID spaceId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Görev başarıyla oluşturulmalıdır")
    void createTask_ShouldReturnTaskDto_WhenSuccess() {
        // GIVEN
        TaskDto inputDto = new TaskDto(null, "Test Task", "Desc", null, null, null, spaceId, null, null);
        Space space = new Space();
        Task task = new Task();
        Task savedTask = new Task();
        TaskDto expectedDto = new TaskDto(UUID.randomUUID(), "Test Task", "Desc", null, null, null, spaceId, null, null);

        doNothing().when(permissionService).validateTaskAccess(spaceId, userId, PermissionType.EDITOR);
        when(spaceRepository.findById(spaceId)).thenReturn(Optional.of(space));
        when(taskMapper.fromDto(inputDto)).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);
        when(taskMapper.toDto(savedTask)).thenReturn(expectedDto);

        // WHEN
        TaskDto result = taskService.createTask(spaceId, inputDto, userId);

        // THEN
        assertNotNull(result);
        assertEquals("Test Task", result.title());
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("Alan bulunamadığında ResourceNotFoundException fırlatmalıdır")
    void createTask_ShouldThrowException_WhenSpaceNotFound() {
        // GIVEN
        TaskDto inputDto = new TaskDto(null, "Hatalı Task", "Desc", null, null, null, spaceId, null, null);

        when(spaceRepository.findById(spaceId)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(spaceId, inputDto, userId);
        });

        verify(taskRepository, never()).save(any());
    }
}

