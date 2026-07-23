package com.dealership.service;

import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.entity.User;
import com.dealership.enums.Role;
import com.dealership.exception.DuplicateResourceException;
import com.dealership.repository.UserRepository;
import com.dealership.service.impl.AuthServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {


    @Mock
    private UserRepository userRepository;


    @Mock
    private PasswordEncoder passwordEncoder;


    @InjectMocks
    private AuthServiceImpl authService;


    @BeforeEach
    void setup(){

        MockitoAnnotations.openMocks(this);

    }



    @Test
    void shouldRegisterUserSuccessfully(){

        RegisterRequest request =
                RegisterRequest.builder()
                        .firstName("John")
                        .lastName("Smith")
                        .email("john@gmail.com")
                        .password("password123")
                        .phone("9999999999")
                        .role(Role.CUSTOMER)
                        .build();


        when(userRepository.existsByEmail(request.getEmail()))
                .thenReturn(false);


        when(passwordEncoder.encode("password123"))
                .thenReturn("encryptedPassword");


        User savedUser = User.builder()
                .id(1L)
                .firstName("John")
                .email("john@gmail.com")
                .password("encryptedPassword")
                .role(Role.CUSTOMER)
                .build();


        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);



        AuthResponse response =
                authService.register(request);



        assertThat(response).isNotNull();

        verify(userRepository)
                .save(any(User.class));


        verify(passwordEncoder)
                .encode("password123");

    }





    @Test
    void shouldRejectDuplicateEmail(){


        RegisterRequest request =
        		RegisterRequest.builder()
                        .email("john@gmail.com")
                        .password("password123")
                        .build();



        when(userRepository.existsByEmail("john@gmail.com"))
                .thenReturn(true);



        assertThatThrownBy(() ->
                authService.register(request))
                .isInstanceOf(DuplicateResourceException.class);



        verify(userRepository,never())
                .save(any());

    }


    @Test
    void passwordShouldBeEncrypted(){


        RegisterRequest request =
                RegisterRequest.builder()
                        .email("test@gmail.com")
                        .password("123456")
                        .role(Role.CUSTOMER)
                        .build();


        when(userRepository.existsByEmail(anyString()))
                .thenReturn(false);


        when(passwordEncoder.encode("123456"))
                .thenReturn("encodedPassword");


        User savedUser = User.builder()
                .id(1L)
                .email("test@gmail.com")
                .password("encodedPassword")
                .role(Role.CUSTOMER)
                .build();


        when(userRepository.save(any(User.class)))
                .thenReturn(savedUser);



        AuthResponse response =
                authService.register(request);



        verify(passwordEncoder)
                .encode("123456");


        verify(userRepository)
                .save(any(User.class));

    }

}