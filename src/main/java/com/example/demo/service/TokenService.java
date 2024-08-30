package com.example.demo.service;

import com.example.demo.model.Token;

public interface TokenService {
    Long saveToken(Token token);
    String deleteToken(String username);
}
