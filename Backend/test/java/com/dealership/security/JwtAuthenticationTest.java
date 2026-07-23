package com.dealership.security;


import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


/**
 * Focused unit tests for {@link JwtUtil} covering the token states the
 * authentication filter has to deal with: valid, invalid, expired and
 * malformed/missing tokens.
 */
class JwtAuthenticationTest {

    private static final String SECRET =
            "RonakCarDealershipJWTSecretKey2026@Secure#Inventory$System";

    private static final long EXPIRATION = 86_400_000L;

    private final JwtUtil jwtUtil = new JwtUtil(SECRET, EXPIRATION);


    @Test
    void validTokenIsAcceptedAndClaimsAreReadable() {

        String token = jwtUtil.generateToken("john@gmail.com", "CUSTOMER");

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractEmail(token)).isEqualTo("john@gmail.com");
        assertThat(jwtUtil.extractRole(token)).isEqualTo("CUSTOMER");
    }


    @Test
    void expiredTokenFailsValidation() {

        // A JwtUtil configured with an expiration in the past so any
        // token it mints is already expired the moment it is issued.
        JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -1000L);

        String expiredToken =
                expiredJwtUtil.generateToken("jane@gmail.com", "ADMIN");

        assertThat(expiredJwtUtil.validateToken(expiredToken)).isFalse();

        assertThatThrownBy(() -> expiredJwtUtil.extractEmail(expiredToken))
                .isInstanceOf(ExpiredJwtException.class);
    }


    @Test
    void invalidTokenFailsValidation() {

        String invalidToken = "this.is.not-a-valid-jwt";

        assertThat(jwtUtil.validateToken(invalidToken)).isFalse();

        assertThatThrownBy(() -> jwtUtil.extractEmail(invalidToken))
                .isInstanceOf(JwtException.class);
    }


    @Test
    void tokenSignedWithDifferentSecretIsRejected() {

        JwtUtil otherIssuer = new JwtUtil(
                "SomeCompletelyDifferentSigningSecretKeyValue123456",
                EXPIRATION
        );

        String tokenFromOtherIssuer =
                otherIssuer.generateToken("mallory@gmail.com", "ADMIN");

        assertThat(jwtUtil.validateToken(tokenFromOtherIssuer)).isFalse();
    }


    @Test
    void emptyOrBlankTokenIsRejected() {

        assertThat(jwtUtil.validateToken("")).isFalse();
    }
}
