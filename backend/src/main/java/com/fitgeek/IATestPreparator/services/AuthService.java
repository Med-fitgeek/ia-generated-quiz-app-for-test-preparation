package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.LoginRequestDto;
import com.fitgeek.IATestPreparator.dtos.LoginResponseDto;
import com.fitgeek.IATestPreparator.dtos.RegisterRequestDto;
import com.fitgeek.IATestPreparator.dtos.RegisterResponseDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<LoginResponseDto> register(RegisterRequestDto dto);
    ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto);
    ResponseEntity<LoginResponseDto> refresh(String refreshToken);
}
