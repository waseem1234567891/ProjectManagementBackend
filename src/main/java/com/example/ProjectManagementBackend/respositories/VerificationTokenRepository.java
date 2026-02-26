package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, UUID> {
    VerificationToken findByToken(String token);
}
