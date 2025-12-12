package com.example.ProjectManagementBackend.respositories;

import com.example.ProjectManagementBackend.models.PasswordResetToken;
import com.example.ProjectManagementBackend.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PasswordResetTokenRepo extends JpaRepository<PasswordResetToken,Long> {
    PasswordResetToken findByToken(String token);

    // Find all tokens for a user
    List<PasswordResetToken> findByUser(User user);
}
