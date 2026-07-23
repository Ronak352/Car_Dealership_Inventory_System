package com.dealership.service;

import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import com.dealership.repository.UserRepository;
import com.dealership.security.JwtUtil;
import com.dealership.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginSuccessfully() {

        LoginRequest request =
                LoginRequest.builder()
                        .email("john@gmail.com")
                        .password("password123")
                        .build();

        User user =
                User.builder()
                        .id(1L)
                        .email("john@gmail.com")
                        .password("encodedPassword")
                        .role(Role.CUSTOMER)
                        .build();

        when(userRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(user));

        when(jwtUtil.generateToken(user.getEmail(), user.getRole().name()))
                .thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");

        verify(authenticationManager).authenticate(any());
    }

}
