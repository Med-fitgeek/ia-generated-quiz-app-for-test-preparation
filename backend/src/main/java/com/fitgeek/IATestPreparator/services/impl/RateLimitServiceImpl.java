package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.services.RateLimitService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitServiceImpl implements RateLimitService {

    private static final int MAX_REQUESTS = 5;
    private static final long WINDOW_SECONDS = 60;

    private final Map<Long, UserRateLimit> limits = new ConcurrentHashMap<>();

    @Override
    public void checkQuizGenerationLimit(Long userId) {

        long now = Instant.now().getEpochSecond();

        UserRateLimit userLimit = limits.computeIfAbsent(
                userId,
                id -> new UserRateLimit(0, now)
        );

        if (now - userLimit.windowStart >= WINDOW_SECONDS) {
            userLimit.requestCount = 0;
            userLimit.windowStart = now;
        }

        userLimit.requestCount++;

        if (userLimit.requestCount > MAX_REQUESTS) {
            throw new BusinessException(
                    "Too many quiz generation requests. Please wait.",
                    HttpStatus.TOO_MANY_REQUESTS
            );
        }
    }

    private static class UserRateLimit {

        int requestCount;
        long windowStart;

        UserRateLimit(int requestCount, long windowStart) {
            this.requestCount = requestCount;
            this.windowStart = windowStart;
        }
    }
}