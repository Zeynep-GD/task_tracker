package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.UserDto;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.mappers.UserMapper;
import com.example.task_tracker.repositories.UserRepository;
import com.example.task_tracker.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Cacheable(value = "users", key = "#id")
    public UserDto getUserDtoById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı"));
    }
}
