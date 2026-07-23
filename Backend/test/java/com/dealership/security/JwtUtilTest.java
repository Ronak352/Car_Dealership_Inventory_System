package com.dealership.security;


import org.junit.jupiter.api.Test;


import static org.assertj.core.api.Assertions.assertThat;



class JwtUtilTest {



    private final String SECRET =
            "RonakCarDealershipJWTSecretKey2026@Secure#Inventory$System";



    private final long EXPIRATION =
            86400000;



    @Test
    void shouldGenerateAndValidateToken(){



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



        assertThat(token)
                .isNotNull();



        assertThat(
                jwtUtil.validateToken(token)
        )
        .isTrue();


    }





    @Test
    void shouldExtractEmailFromToken(){



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
                .isEqualTo(
                        "john@gmail.com"
                );


    }




    @Test
    void shouldExtractRoleFromToken(){



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



        String role =
                jwtUtil.extractRole(token);



        assertThat(role)
                .isEqualTo(
                        "CUSTOMER"
                );


    }


}