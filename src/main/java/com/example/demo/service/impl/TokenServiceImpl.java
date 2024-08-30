package com.example.demo.service.impl;

import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.model.Token;
import com.example.demo.repository.TokenRepository;
import com.example.demo.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private final TokenRepository tokenRepository;

    @Override
    @Transactional
    public Long saveToken(Token token) {
        return tokenRepository.findByUsername(token.getUsername())
                .map(existingToken -> {
                    existingToken.setAccessToken(token.getAccessToken());
                    existingToken.setRefreshToken(token.getRefreshToken());
                    return existingToken.getId();
                })
                .orElseGet(() -> tokenRepository.save(token).getId());
    }

    @Override
    public String deleteToken(String username) {
        Token token = getByUsername(username);
        tokenRepository.delete(token);
        return "Token deleted";
    }

    private Token getByUsername(String username) {
        return tokenRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Token not found"));
    }
}
