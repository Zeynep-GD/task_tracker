package com.example.task_tracker.controller;

import com.example.task_tracker.domain.dto.PermissionRequest;
import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.services.PermissionService;
import com.example.task_tracker.services.SpaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/spaces")
@RequiredArgsConstructor
public class SpaceController {

    private final SpaceService spaceService;
    private final PermissionService permissionService;

    @GetMapping
    public ResponseEntity<List<SpaceDto>> getAllSpaces(@AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.listSpaces(userId));
    }

    @PostMapping
    public ResponseEntity<SpaceDto> createSpace(@Valid @RequestBody SpaceDto spaceDto,
                                                @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.createSpace(spaceDto, userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpaceDto> getSpace(@PathVariable UUID id,
                                             @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.getSpace(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpaceDto> updateSpace(@PathVariable UUID id, @Valid @RequestBody SpaceDto spaceDto,
                                                @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.updateSpace(id, userId, spaceDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<SpaceDto> patchSpace(@PathVariable UUID id, @RequestBody SpaceDto spaceDto,
                                               @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.patchSpace(id, userId, spaceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSpace(@PathVariable UUID id, @AuthenticationPrincipal(expression = "id") UUID userId) {
        spaceService.deleteSpace(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> grantPermission(@PathVariable UUID id, @Valid @RequestBody PermissionRequest request,
                                                @AuthenticationPrincipal(expression = "id") UUID userId) {
        permissionService.grantPermission(id, request, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<SpaceDto>> getNearbySpaces(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam double radius,
            @AuthenticationPrincipal(expression = "id") UUID userId) {
        return ResponseEntity.ok(spaceService.getNearbySpaces(lat, lon, radius, userId));
    }
}