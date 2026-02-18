package com.example.task_tracker.services;

import com.example.task_tracker.domain.dto.AuthenticationResponse;
import com.example.task_tracker.domain.dto.LoginRequest;
import com.example.task_tracker.domain.dto.RegisterRequest;

public interface AuthenticationService {
    AuthenticationResponse register(RegisterRequest request);
    AuthenticationResponse login(LoginRequest request);
}
