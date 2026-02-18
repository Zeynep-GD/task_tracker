package com.example.task_tracker.domain.dto;

import java.io.Serializable;

public record LocationDto(
        double latitude,
        double longitude
) implements Serializable {
    private static final long serialVersionUID = 1L;
}

