package com.dealership.service.impl;

import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.entity.User;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.exception.ResourceNotFoundException;
import com.dealership.repository.UserRepository;
import com.dealership.security.JwtUtil;
import com.dealership.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "User already exists with email: " + request.getEmail());
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        User savedUser = userRepository.save(user);

        return AuthResponse.builder()
                .userId(savedUser.getId())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().name())
                .token(null)
                .build();
    }

    
    @Override
    public AuthResponse login(LoginRequest request) {
    	
    	authenticationManager.authenticate(
    	        new UsernamePasswordAuthenticationToken(
    	                request.getEmail(),
    	                request.getPassword()
    	        )
    	);

    	User user = userRepository.findByEmail(request.getEmail())
    	        .orElseThrow(() ->
    	                new ResourceNotFoundException("User not found"));

    	String token = jwtUtil.generateToken(
    	        user.getEmail(),
    	        user.getRole().name()
    	);

    	return AuthResponse.builder()
    	        .userId(user.getId())
    	        .email(user.getEmail())
    	        .role(user.getRole().name())
    	        .token(token)
    	        .build();

       
    }
}