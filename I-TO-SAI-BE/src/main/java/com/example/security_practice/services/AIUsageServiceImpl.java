package com.example.security_practice.services;

import com.example.security_practice.entities.DailyUseRequestId;
import com.example.security_practice.entities.DailyUserRequestCount;
import com.example.security_practice.entities.UserUsage;
import com.example.security_practice.repositories.DailyUserRequestCountRepository;
import com.example.security_practice.repositories.UserUsageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AIUsageServiceImpl implements AiUsageService {
    private final UserUsageRepository userUsageRepo;
    private final DailyUserRequestCountRepository dailyUserRequestCountRepo;
    @Value("${openai.daily-request-limit}")
    private int DAILY_LIMIT;

    @Override
    public void validateDailyLimit(String userId) {
        LocalDate today = LocalDate.now();
        DailyUseRequestId id = new DailyUseRequestId(userId, today);
        DailyUserRequestCount count = dailyUserRequestCountRepo.findById(id).orElseGet(() -> new DailyUserRequestCount(id, 0));

        if (count.getCount() >= DAILY_LIMIT) {
            throw new RateLimitExceededException(
                    "Daily request limit of " + DAILY_LIMIT + " exceeded"
            );
        }

        count.setCount(count.getCount()+1);
        dailyUserRequestCountRepo.save(count);
    }

    @Override
    public void incrementRequestCount(String userId) {
        UserUsage u = userUsageRepo.findById(userId)
                .orElseGet(() -> new UserUsage(userId, 0, 0));
        u.setRequestCount(u.getRequestCount() + 1);
        userUsageRepo.save(u);
    }

    @Override
    public void addTokensUsed(String userId, long tokens) {
        UserUsage u = userUsageRepo.findById(userId)
                .orElseGet(() -> new UserUsage(userId, 0, 0));
        u.setTokensUsed(u.getTokensUsed() + tokens);
        userUsageRepo.save(u);
    }
}


