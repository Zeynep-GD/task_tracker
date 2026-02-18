package com.example.task_tracker.mappers.impl;

import com.example.task_tracker.domain.dto.RegisterRequest;
import com.example.task_tracker.domain.dto.UserDto;
import com.example.task_tracker.domain.entities.User;
import com.example.task_tracker.mappers.FileAttachmentMapper;
import com.example.task_tracker.mappers.SpaceMapper;
import com.example.task_tracker.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapperImpl implements UserMapper {
    private final SpaceMapper spaceMapper;
    private final FileAttachmentMapper fileMapper;

    public UserMapperImpl(SpaceMapper spaceMapper, FileAttachmentMapper fileMapper) {
        this.spaceMapper = spaceMapper;
        this.fileMapper = fileMapper;
    }
    @Override
    public User fromRegisterRequest(RegisterRequest registerRequest) {
        if (registerRequest == null) {
            return null;
        }
        // Builder kullanarak Entity oluşturma
        return User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password(registerRequest.password())
                .build();
    }
    @Override
    public UserDto toDto(User user) {
        if (user == null) return null;

        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getSpaces() != null ? user.getSpaces().stream()
                        .map(spaceMapper::toDto)
                        .collect(Collectors.toList()) : null,
                user.getProfilePicture() != null
                        ? fileMapper.toDto(user.getProfilePicture())
                        : null
        );
    }
}
