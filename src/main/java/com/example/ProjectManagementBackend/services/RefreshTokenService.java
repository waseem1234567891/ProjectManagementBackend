package com.example.ProjectManagementBackend.services;

import com.example.ProjectManagementBackend.models.RefreshToken;
import com.example.ProjectManagementBackend.models.User;
import com.example.ProjectManagementBackend.respositories.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public RefreshToken createRefreshToken(User user) {

        // 🔥 delete old token
        refreshTokenRepository.deleteByUser(user);

        RefreshToken token = new RefreshToken();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(7, ChronoUnit.DAYS));
        token.setRevoked(false);

        return refreshTokenRepository.save(token); // 🔥 THIS LINE SAVES IT
    }
}
