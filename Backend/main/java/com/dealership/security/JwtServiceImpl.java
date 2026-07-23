package com.dealership.security;


import com.dealership.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {



    private final JwtUtil jwtUtil;



    @Override
    public String generateToken(User user) {


        return jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

    }

}