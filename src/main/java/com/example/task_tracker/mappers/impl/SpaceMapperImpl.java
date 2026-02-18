package com.example.task_tracker.mappers.impl;

import com.example.task_tracker.domain.dto.LocationDto;
import com.example.task_tracker.domain.dto.SpaceDto;
import com.example.task_tracker.domain.entities.Space;
import com.example.task_tracker.mappers.FileAttachmentMapper;
import com.example.task_tracker.mappers.SpaceMapper;
import com.example.task_tracker.mappers.TaskMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;


@Component
public class SpaceMapperImpl implements SpaceMapper {
    private final TaskMapper taskMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    private final FileAttachmentMapper fileMapper;

    public SpaceMapperImpl(TaskMapper taskMapper, FileAttachmentMapper fileMapper) {
        this.taskMapper = taskMapper;
        this.fileMapper = fileMapper;
    }
    @Override
    public Space fromDto(SpaceDto spaceDto) {
        if (spaceDto == null) return null;
        Space space = new Space();
        space.setId(spaceDto.id());
        space.setTitle(spaceDto.title());
        space.setDescription(spaceDto.description());

        if (spaceDto.centerLocation() != null) {
            space.setCenterLocation(geometryFactory.createPoint(new Coordinate(
                    spaceDto.centerLocation().longitude(),
                    spaceDto.centerLocation().latitude()
            )));
        }
        return space;
    }

    @Override
    public SpaceDto toDto(Space space) {
        if (space == null) return null;

        LocationDto centerDto = null;
        if (space.getCenterLocation() != null) {
            centerDto = new LocationDto(space.getCenterLocation().getY(), space.getCenterLocation().getX());
        }

        return new SpaceDto(
                space.getId(),
                space.getTitle(),
                space.getDescription(),
                space.getOwner() != null ? space.getOwner().getId() : null,
                space.getTasks() != null ? space.getTasks().stream().map(taskMapper::toDto).collect(Collectors.toList()) : null,
                centerDto,
                space.getAttachments() != null
                        ? space.getAttachments().stream().map(fileMapper::toDto).collect(Collectors.toList())
                        : null
        );
    }
}