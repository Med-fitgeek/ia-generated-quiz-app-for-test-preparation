package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface QuizSessionService {

    SessionResponseDto getOrCreateSession(Long quizId);

    ResultResponseDto submitSession(Long sessionId, SubmitSessionRequestDto request);

    @Transactional(readOnly = true)
    SessionResponseDto getSessionById(Long sessionId);

    @Transactional(readOnly = true)
    Page<SessionResponseDto> getAllSessionsByOwner(Pageable pageable);

    @Transactional
    void deleteSession(Long sessionId);

    QuizReviewDto getSessionResult(Long sessionId);

    @Transactional(readOnly = true)
    QuizReportDto getSessionReport(Long sessionId);
}
