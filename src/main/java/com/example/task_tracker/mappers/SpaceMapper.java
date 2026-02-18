package com.example.task_tracker.mappers;

import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.domain.entities.Space;

public interface SpaceMapper {
    Space fromDto(SpaceDto spaceDto);
    SpaceDto toDto(Space space);
}