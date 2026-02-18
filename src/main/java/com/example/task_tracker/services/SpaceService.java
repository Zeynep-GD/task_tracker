package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.SpaceDto;

import java.util.List;
import java.util.UUID;

public interface SpaceService {
    public List<SpaceDto> listSpaces(UUID userId);
    SpaceDto createSpace(SpaceDto spaceDto, UUID userId);
    SpaceDto getSpace(UUID id, UUID userId);
    SpaceDto updateSpace(UUID id, UUID userId, SpaceDto spaceDto);
    SpaceDto patchSpace(UUID id,UUID userId, SpaceDto spaceDto);
    void deleteSpace(UUID id,UUID userId);
    List<SpaceDto> getNearbySpaces(double lat, double lon, double radius, UUID userId);
}
