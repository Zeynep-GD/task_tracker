package com.example.task_tracker.controller;

import com.example.task_tracker.domain.dto.PermissionRequest;
import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.services.PermissionService;
import com.example.task_tracker.services.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/spaces/{spaceId}/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<TaskDto>> listTasks(@PathVariable UUID spaceId,
                                                   @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.listTasks(spaceId, userId));
    }

    @PostMapping
    public ResponseEntity<TaskDto> createTask(@PathVariable UUID spaceId, @Valid @RequestBody TaskDto taskDto,
                                              @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.createTask(spaceId, taskDto, userId));
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDto> getTask(@PathVariable UUID spaceId, @PathVariable UUID taskId,
                                           @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.getTask(spaceId, taskId, userId));
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDto> updateTask(@PathVariable UUID spaceId, @PathVariable UUID taskId, @Valid @RequestBody TaskDto taskDto,
                                              @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.updateTask(spaceId, taskId, taskDto, userId));
    }

    @PatchMapping("/{taskId}")
    public ResponseEntity<TaskDto> patchTask(@PathVariable UUID spaceId, @PathVariable UUID taskId, @RequestBody TaskDto taskDto,
                                             @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.patchTask(spaceId, taskId, taskDto, userId));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID spaceId, @PathVariable UUID taskId,
                                           @AuthenticationPrincipal(expression = "id") UUID userId) {
        taskService.deleteTask(spaceId, taskId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{taskId}/permissions")
    public ResponseEntity<Void> grantTaskPermission(
            @PathVariable UUID spaceId, // URL tutarlılığı için var ama service'te taskId yetiyor
            @PathVariable UUID taskId,
            @Valid @RequestBody PermissionRequest request,
            @AuthenticationPrincipal(expression = "id") UUID userId) {

        permissionService.grantTaskPermission(taskId, request, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<TaskDto>> getNearbyTasks(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius,
            @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(taskService.getNearbyTasks(lat, lon, radius, userId));
    }
}