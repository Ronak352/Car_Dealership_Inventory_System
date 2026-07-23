package com.dealership.security;


import lombok.RequiredArgsConstructor;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;


import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;


import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



/**
 * Central security configuration.
 *
 * Coarse-grained rules (which paths need a bearer token at all) live here.
 * Fine-grained, per-role authorization for each endpoint is expressed with
 * {@code @PreAuthorize} on the individual controller methods, which keeps the
 * role matrix next to the operation it protects instead of a single giant
 * matcher list here that drifts out of sync with the controllers.
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {



    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;




    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {



        return http

                .csrf(csrf -> csrf.disable())


                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )


                .exceptionHandling(exception -> exception

                        // No valid Authentication at all (missing / invalid / expired token)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)

                        // Authenticated, but the role doesn't allow this operation
                        .accessDeniedHandler(customAccessDeniedHandler)

                )


                .authorizeHttpRequests(auth -> auth


                        .requestMatchers(
                                "/api/auth/**"
                        )
                        .permitAll()


                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        )
                        .permitAll()


                        .requestMatchers(
                                "/error"
                        )
                        .permitAll()


                        .anyRequest()
                        .authenticated()

                )


                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )


                .build();

    }










    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    )
            throws Exception {


        return configuration.getAuthenticationManager();

    }


}