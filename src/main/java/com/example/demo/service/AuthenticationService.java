package com.example.demo.service;

import com.example.demo.dto.request.RegisterRequest;
import com.example.demo.dto.request.SignInRequest;
import com.example.demo.dto.response.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenticationService {
    TokenResponse authenticate(SignInRequest request);
    TokenResponse register(RegisterRequest request);
    TokenResponse refresh(HttpServletRequest request);
    String logout(HttpServletRequest request);
}
