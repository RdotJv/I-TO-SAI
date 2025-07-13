package com.example.security_practice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("production")
class DatabaseConnectionTest {

    @Test
    void contextLoads() {
        // This test will verify that the application can start with production profile
        // and connect to the database
    }
} 