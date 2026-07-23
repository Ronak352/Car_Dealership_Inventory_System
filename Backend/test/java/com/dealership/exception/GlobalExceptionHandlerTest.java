package com.dealership.exception;


import jakarta.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.assertj.core.api.Assertions.assertThat;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;



class GlobalExceptionHandlerTest {



    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();



    private final HttpServletRequest request =
            mock(HttpServletRequest.class);




    // ==========================================
    // RESOURCE NOT FOUND TEST
    // ==========================================


    @Test
    void shouldHandleResourceNotFoundException() {


        when(request.getRequestURI())
                .thenReturn("/api/vehicles/10");



        ResourceNotFoundException exception =
                new ResourceNotFoundException(
                        "Vehicle not found with id : 10"
                );



        ResponseEntity<ErrorResponse> response =
                handler.handleResourceNotFound(
                        exception,
                        request
                );



        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);



        assertThat(response.getBody().getMessage())
                .isEqualTo(
                        "Vehicle not found with id : 10"
                );

    }





    // ==========================================
    // USER NOT FOUND TEST
    // ==========================================


    @Test
    void shouldHandleUserNotFoundException() {


        when(request.getRequestURI())
                .thenReturn("/api/users/5");



        UserNotFoundException exception =
                new UserNotFoundException(
                        "User not found"
                );



        ResponseEntity<ErrorResponse> response =
                handler.handleUserNotFound(
                        exception,
                        request
                );



        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);



        assertThat(response.getBody().getError())
                .isEqualTo("User Not Found");

    }





    // ==========================================
    // DUPLICATE RESOURCE TEST
    // ==========================================


    @Test
    void shouldHandleDuplicateResourceException() {


        when(request.getRequestURI())
                .thenReturn("/api/customers");



        DuplicateResourceException exception =
                new DuplicateResourceException(
                        "Email already exists"
                );



        ResponseEntity<ErrorResponse> response =
                handler.handleDuplicateResource(
                        exception,
                        request
                );



        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);



        assertThat(response.getBody().getMessage())
                .isEqualTo(
                        "Email already exists"
                );

    }





    // ==========================================
    // INVALID CREDENTIAL TEST
    // ==========================================


    @Test
    void shouldHandleInvalidCredentialException() {


        when(request.getRequestURI())
                .thenReturn("/api/auth/login");



        InvalidCredentialsException exception =
                new InvalidCredentialsException(
                        "Invalid email or password"
                );



        ResponseEntity<ErrorResponse> response =
                handler.handleInvalidCredential(
                        exception,
                        request
                );



        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);



        assertThat(response.getBody().getError())
                .isEqualTo(
                        "Invalid Credentials"
                );

    }





    // ==========================================
    // GLOBAL EXCEPTION TEST
    // ==========================================


    @Test
    void shouldHandleGenericException() {


        when(request.getRequestURI())
                .thenReturn("/api/test");



        Exception exception =
                new Exception(
                        "Something went wrong"
                );



        ResponseEntity<ErrorResponse> response =
                handler.handleGlobalException(
                        exception,
                        request
                );



        assertThat(response.getStatusCode())
                .isEqualTo(
                        HttpStatus.INTERNAL_SERVER_ERROR
                );



        assertThat(response.getBody().getMessage())
                .isEqualTo(
                        "Something went wrong"
                );

    }

}