package com.dealership.service;


import com.dealership.dto.request.LoginRequest;
import com.dealership.dto.request.RegisterRequest;
import com.dealership.dto.response.AuthResponse;


public interface AuthService {


    AuthResponse register(RegisterRequest request);


    AuthResponse login(LoginRequest request);

}