package com.example.task_tracker.mappers;

import com.example.task_tracker.domain.dto.RegisterRequest;
import com.example.task_tracker.domain.dto.UserDto;
import com.example.task_tracker.domain.entities.User;

public interface UserMapper {
    User fromRegisterRequest(RegisterRequest registerRequest);
    UserDto toDto(User user);
}
