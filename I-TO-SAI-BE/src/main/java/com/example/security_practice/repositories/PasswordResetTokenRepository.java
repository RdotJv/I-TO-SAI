package com.example.security_practice.repositories;

import com.example.security_practice.entities.PasswordResetToken;
import com.example.security_practice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteAllByUser(User user);
}
