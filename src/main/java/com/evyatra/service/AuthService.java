package com.evyatra.service;

import com.evyatra.exception.BadRequestException;
import com.evyatra.exception.ResourceNotFoundException;
import com.evyatra.dto.AuthResponse;
import com.evyatra.dto.LoginRequest;
import com.evyatra.dto.RegisterRequest;
import com.evyatra.model.User;
import com.evyatra.repository.UserRepository;
import com.evyatra.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor    // Lombok — constructor injection auto karta hai
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;  // bcrypt
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {

        // Is Email already exist ?
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered hai!");
        }

        // Create User object
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // encrypt
        user.setPhone(request.getPhone());
        user.setRole(User.Role.ROLE_USER);

        // Save in Database
        userRepository.save(user);

        // generate the JWT Token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {

        // Search user by Email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User nahi mila!"));

        // check Password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Password galat hai!");
        }

        // generate Token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}