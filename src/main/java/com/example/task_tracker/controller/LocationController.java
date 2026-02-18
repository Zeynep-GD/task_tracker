package com.example.task_tracker.controller;

import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.services.SpaceService;
import com.example.task_tracker.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/location")
@RequiredArgsConstructor
public class LocationController {

    private final TaskService taskService;
    private final SpaceService spaceService;

    //Yakındaki Taskları Getirme
    @GetMapping("/nearby-tasks")
    public ResponseEntity<?> getNearbyTasks(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius,
            @AuthenticationPrincipal(expression = "id") UUID userId) {

        List<TaskDto> tasks = taskService.getNearbyTasks(lat, lon, radius, userId);

        if (tasks.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of("message", "Yakınınızda herhangi bir task bulunamadı."));
        }

        return ResponseEntity.ok(tasks);
    }

    //Yakındaki Spaceleri Getirme
    @GetMapping("/nearby-spaces")
    public ResponseEntity<?> getNearbySpaces(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius,
            @AuthenticationPrincipal(expression = "id") UUID userId) {

        List<SpaceDto> spaces = spaceService.getNearbySpaces(lat, lon, radius, userId);

        if (spaces.isEmpty()) {
            return ResponseEntity.ok(java.util.Map.of("message", "Yakınınızda herhangi bir space bulunamadı."));
        }

        return ResponseEntity.ok(spaces);
    }
}