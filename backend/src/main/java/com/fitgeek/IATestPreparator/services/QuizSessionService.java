package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.ResultResponseDto;
import com.fitgeek.IATestPreparator.dtos.SessionResponseDto;
import com.fitgeek.IATestPreparator.dtos.SubmitSessionRequestDto;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface QuizSessionService {

    SessionResponseDto getOrCreateSession(UserDetails userDetails, Long quizId);

    ResultResponseDto submitSession(
            UserDetails userDetails,
            Long sessionId,
            SubmitSessionRequestDto request
    );

    @Transactional(readOnly = true)
    SessionResponseDto getSessionById(Long sessionId, UserDetails userDetails);

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    List<SessionResponseDto> getAllSessionsByOwner(UserDetails userDetails);

    @org.springframework.transaction.annotation.Transactional
    void deleteSession(Long sessionId, UserDetails userDetails);
}
