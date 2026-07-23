package com.dealership.security;


import com.dealership.enums.Role;
import com.dealership.entity.User;


import org.junit.jupiter.api.Test;


import org.springframework.security.core.context.SecurityContextHolder;


import static org.assertj.core.api.Assertions.assertThat;



class JwtAuthenticationFilterTest {



    private final String SECRET =
            "RonakCarDealershipJWTSecretKey2026@Secure#Inventory$System";


    private final long EXPIRATION =
            86400000;



    @Test
    void validTokenShouldAuthenticateUser(){


        JwtUtil jwtUtil =
                new JwtUtil(
                        SECRET,
                        EXPIRATION
                );


        String token =
                jwtUtil.generateToken(
                        "john@gmail.com",
                        "CUSTOMER"
                );


        String email =
                jwtUtil.extractEmail(token);



        assertThat(email)
                .isEqualTo("john@gmail.com");


        assertThat(
                jwtUtil.validateToken(token)
        )
        .isTrue();


    }





    @Test
    void invalidTokenShouldFailValidation(){


        JwtUtil jwtUtil =
                new JwtUtil(
                        SECRET,
                        EXPIRATION
                );


        boolean result =
                jwtUtil.validateToken(
                        "invalid-token"
                );



        assertThat(result)
                .isFalse();


    }


}