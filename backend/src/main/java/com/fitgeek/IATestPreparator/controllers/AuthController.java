package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.LoginRequestDto;
import com.fitgeek.IATestPreparator.dtos.LoginResponseDto;
import com.fitgeek.IATestPreparator.dtos.RegisterRequestDto;
import com.fitgeek.IATestPreparator.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(
            @RequestBody @Valid RegisterRequestDto dto
    ) {
        return authService.register(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody @Valid LoginRequestDto dto
    ) {
        return authService.login(dto);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refresh(
            @CookieValue("refreshToken") String refreshToken
    ) {
        return authService.refresh(refreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        ResponseCookie deleteCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false) // true en prod HTTPS
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }
}
