package com.fitgeek.memorix.services;

import com.fitgeek.memorix.dtos.LoginRequestDto;
import com.fitgeek.memorix.dtos.LoginResponseDto;
import com.fitgeek.memorix.dtos.RegisterRequestDto;
import org.springframework.http.ResponseEntity;

public interface AuthService {

    ResponseEntity<LoginResponseDto> register(RegisterRequestDto dto);
    ResponseEntity<LoginResponseDto> login(LoginRequestDto loginRequestDto);
    ResponseEntity<LoginResponseDto> refresh(String refreshToken);
}
