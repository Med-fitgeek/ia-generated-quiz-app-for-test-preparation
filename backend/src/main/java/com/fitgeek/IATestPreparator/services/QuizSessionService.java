package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.ResultResponseDto;
import com.fitgeek.IATestPreparator.dtos.SessionRequestDto;
import com.fitgeek.IATestPreparator.dtos.SessionResponseDto;
import com.fitgeek.IATestPreparator.dtos.SubmitSessionRequestDto;
import org.springframework.security.core.userdetails.UserDetails;

public interface QuizSessionService {

    SessionResponseDto createSession(UserDetails userDetails, SessionRequestDto sessionRequestDto);

    SessionResponseDto startSession(UserDetails userDetails, Long sessionId);

    SessionResponseDto markFailed(UserDetails userDetails, Long sessionId);

    ResultResponseDto submitSession(
            UserDetails userDetails,
            Long sessionId,
            SubmitSessionRequestDto request
    );
}
