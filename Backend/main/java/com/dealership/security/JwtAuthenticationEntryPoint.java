package com.dealership.security;


import com.dealership.exception.ErrorResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;


/**
 * Handles every request that reaches an authenticated endpoint without a
 * valid Authentication in the SecurityContext, and returns a consistent
 * 401 JSON response instead of the default Spring Security HTML/plain page.
 *
 * The specific reason (missing / invalid / expired token) is read from the
 * request attribute populated by {@link JwtAuthenticationFilter}.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    public static final String JWT_ERROR_ATTRIBUTE = "jwt_error";

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        String reason = (String) request.getAttribute(JWT_ERROR_ATTRIBUTE);

        if (reason == null) {
            reason = "Authentication is required to access this resource";
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error("Unauthorized")
                .message(reason)
                .path(request.getRequestURI())
                .build();

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
