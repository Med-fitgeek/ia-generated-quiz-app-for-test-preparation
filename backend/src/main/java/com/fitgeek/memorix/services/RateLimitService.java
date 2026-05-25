package com.fitgeek.memorix.services;

public interface RateLimitService {

    void checkQuizGenerationLimit(Long userId);

}