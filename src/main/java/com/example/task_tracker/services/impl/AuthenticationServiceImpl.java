package com.example.task_tracker.services.impl;

import com.example.task_tracker.domain.dto.AuthenticationResponse;
import com.example.task_tracker.domain.dto.LoginRequest;
import com.example.task_tracker.domain.dto.RegisterRequest;
import com.example.task_tracker.domain.entities.Role;
import com.example.task_tracker.domain.entities.User;
import com.example.task_tracker.exception.ResourceAlreadyExistsException;
import com.example.task_tracker.exception.ResourceNotFoundException;
import com.example.task_tracker.repositories.UserRepository;
import com.example.task_tracker.services.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationServiceImpl(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     JwtService jwtService,
                                     AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        //E-posta kontrolü
        if (userRepository.existsByEmail(request.email())) {
            throw new ResourceAlreadyExistsException("Bu e-posta adresi zaten kullanımda: " + request.email());
        }

        //Kullanıcı oluşturma
        var user = User.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) // Şifreyi hashle
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .build();

        //Veritabanına kaydetme
        userRepository.save(user);

        //Token üretme
        var jwtToken = jwtService.generateToken(user);

        //Token'ı kutulayıp geri dönme
        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        //Spring Security ile kullanıcı adı ve şifre kontrolü
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı bulunamadı: " + request.email()));

        var jwtToken = jwtService.generateToken(user);

        return new AuthenticationResponse(jwtToken);
    }

}

