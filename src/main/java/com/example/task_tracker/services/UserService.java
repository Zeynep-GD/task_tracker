package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.UserDto;

import java.util.UUID;

public interface UserService {
    UserDto getUserDtoById(UUID id);
}
