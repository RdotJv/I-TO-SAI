package com.example.security_practice.repositories;

import com.example.security_practice.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationTokenRepository
        extends JpaRepository<EmailVerificationToken,Long> {

    Optional<EmailVerificationToken> findByToken(String token);
}