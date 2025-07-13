package com.example.security_practice.repositories;

import com.example.security_practice.entities.UserUsage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserUsageRepository extends JpaRepository<UserUsage, String> {
}
