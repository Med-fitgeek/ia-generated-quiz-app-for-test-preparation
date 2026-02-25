package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.LoginRequestDto;
import com.fitgeek.IATestPreparator.dtos.LoginResponseDto;
import com.fitgeek.IATestPreparator.dtos.RegisterRequestDto;
import com.fitgeek.IATestPreparator.dtos.RegisterResponseDto;
import com.fitgeek.IATestPreparator.entities.RefreshToken;
import com.fitgeek.IATestPreparator.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        RegisterResponseDto responseDto = authService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequestDto loginRequestDto) {
        LoginResponseDto responseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/api/auth/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        LoginResponseDto response = authService.refreshAccessToken(refreshToken);

        return ResponseEntity.ok(response);
    }
}
