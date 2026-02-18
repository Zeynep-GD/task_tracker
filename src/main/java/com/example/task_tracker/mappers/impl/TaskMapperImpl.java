package com.example.task_tracker.mappers.impl;

import com.example.task_tracker.domain.dto.LocationDto;
import com.example.task_tracker.domain.dto.TaskDto;
import com.example.task_tracker.domain.entities.Task;
import com.example.task_tracker.mappers.TaskMapper;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class TaskMapperImpl implements TaskMapper {

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    private final FileAttachmentMapperImpl fileMapper;

    public TaskMapperImpl(FileAttachmentMapperImpl fileMapper) {
        this.fileMapper = fileMapper;
    }

    @Override
    public Task fromDto(TaskDto taskDto) {
        if (taskDto == null) return null;

        Task task = new Task();
        task.setId(taskDto.id());
        task.setTitle(taskDto.title());
        task.setDescription(taskDto.description());
        task.setDueDate(taskDto.dueDate());
        task.setStatus(taskDto.status());
        task.setPriority(taskDto.priority());

        if (taskDto.location() != null) {
            task.setLocation(geometryFactory.createPoint(new Coordinate(
                    taskDto.location().longitude(),
                    taskDto.location().latitude()
            )));
        }
        return task;
    }

    @Override
    public TaskDto toDto(Task task) {
        if (task == null) return null;

        LocationDto locationDto = null;
        if (task.getLocation() != null) {
            locationDto = new LocationDto(task.getLocation().getY(), task.getLocation().getX());
        }

        return new TaskDto(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getDueDate(),
                task.getPriority(),
                task.getStatus(),
                task.getSpace() != null ? task.getSpace().getId() : null,
                locationDto,
                task.getAttachments() != null
                        ? task.getAttachments().stream().map(fileMapper::toDto).collect(Collectors.toList())
                        : null
        );
    }
}
