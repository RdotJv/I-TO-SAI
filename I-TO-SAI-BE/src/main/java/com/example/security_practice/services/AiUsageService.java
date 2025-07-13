package com.example.security_practice.services;

public interface AiUsageService {
    void validateDailyLimit(String userId);
    void incrementRequestCount(String userId);
    void addTokensUsed(String userId, long tokens);
}
