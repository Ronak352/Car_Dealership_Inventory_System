package com.dealership.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.userdetails.UserDetails;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;


import org.springframework.web.filter.OncePerRequestFilter;


import java.io.IOException;


/**
 * Extracts and validates the JWT bearer token on every request.
 *
 * This filter never rejects a request itself: if the token is missing,
 * malformed, expired, or otherwise invalid, it simply leaves the
 * SecurityContext unauthenticated and records the reason as a request
 * attribute. The actual 401/403 responses are produced later by
 * {@link JwtAuthenticationEntryPoint} and {@link CustomAccessDeniedHandler},
 * so every security failure returns a single consistent JSON shape.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter
        extends OncePerRequestFilter {



    private final JwtUtil jwtUtil;

    private final CustomUserDetailsService userDetailsService;



    @Override
    protected void doFilterInternal(

            HttpServletRequest request,

            HttpServletResponse response,

            FilterChain filterChain

    )
            throws ServletException, IOException {



        String authHeader =
                request.getHeader("Authorization");



        if (authHeader == null || !authHeader.startsWith("Bearer ")) {

            // No token supplied at all. We do not fail here — public
            // endpoints must still be reachable. If the endpoint actually
            // requires authentication, the entry point will report this.
            request.setAttribute(
                    JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                    "Authentication token is missing"
            );

            filterChain.doFilter(request, response);

            return;

        }



        String token = authHeader.substring(7);



        try {

            String email = jwtUtil.extractEmail(token);

            if (email != null
                    && SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {



                if (!jwtUtil.validateToken(token)) {

                    request.setAttribute(
                            JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                            "Authentication token is invalid"
                    );

                    filterChain.doFilter(request, response);

                    return;

                }



                UserDetails userDetails =
                        userDetailsService
                        .loadUserByUsername(email);



                UsernamePasswordAuthenticationToken authentication =

                    new UsernamePasswordAuthenticationToken(

                            userDetails,

                            null,

                            userDetails.getAuthorities()

                    );



                authentication
                    .setDetails(
                        new WebAuthenticationDetailsSource()
                        .buildDetails(request)
                    );



                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);

            }

        } catch (ExpiredJwtException ex) {

            log.debug("Rejected expired JWT: {}", ex.getMessage());

            request.setAttribute(
                    JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                    "Authentication token has expired"
            );

        } catch (MalformedJwtException | SignatureException ex) {

            log.debug("Rejected malformed/invalid JWT signature: {}", ex.getMessage());

            request.setAttribute(
                    JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                    "Authentication token is invalid"
            );

        } catch (UsernameNotFoundException ex) {

            log.debug("Token subject does not match any known user: {}", ex.getMessage());

            request.setAttribute(
                    JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                    "Authentication token is invalid"
            );

        } catch (JwtException | IllegalArgumentException ex) {

            log.debug("Rejected unparseable JWT: {}", ex.getMessage());

            request.setAttribute(
                    JwtAuthenticationEntryPoint.JWT_ERROR_ATTRIBUTE,
                    "Authentication token is invalid"
            );

        }



        filterChain.doFilter(request, response);

    }

}