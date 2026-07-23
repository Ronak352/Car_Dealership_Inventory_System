package com.dealership.security;

import com.dealership.entity.User;

public interface JwtService {

    String generateToken(User user);

}