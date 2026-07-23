package com.dealership.controller;


import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;
import com.dealership.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Registration and login. These endpoints are public and issue the JWT used by every other endpoint.")
@SecurityRequirements // overrides the global bearerAuth requirement: this controller needs no token
public class AuthController {


    private final AuthService authService;



    @Operation(
            summary = "Register a new user",
            description = "Creates a user account (default role CUSTOMER unless specified) and returns an access/refresh token pair."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation failed on the request body"),
            @ApiResponse(responseCode = "409", description = "Email already registered")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request
    ){

        AuthResponse response =
                authService.register(request);


        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);

    }




    @Operation(
            summary = "Log in",
            description = "Validates credentials and returns a signed JWT to use as 'Bearer <token>' on subsequent requests."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful, JWT returned"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ){

        AuthResponse response =
                authService.login(request);


        return ResponseEntity
                .ok(response);

    }

}