package com.retail.order.auth_service.service;

import com.retail.order.auth_service.dto.AuthResponse;
import com.retail.order.auth_service.dto.LoginRequest;
import com.retail.order.auth_service.dto.RegisterRequest;
import com.retail.order.auth_service.entity.User;
import com.retail.order.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public String register(RegisterRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role("ROLE_USER")
                .build();
        userRepository.save(user);
        return "User Registered Successfully";

    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepository

                .findByEmail(request.getEmail())

                .orElseThrow(

                        () -> new RuntimeException("User Not Found")

                );

        if (!passwordEncoder.matches(

                request.getPassword(),

                user.getPassword())) {

            throw new RuntimeException("Invalid Credentials");

        }

        String token = jwtService.generateToken(user);

        return new AuthResponse(token);

    }
}