package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.*;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.services.AuthService;
import com.fitgeek.IATestPreparator.services.CurrentUserService;
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
    private final CurrentUserService currentUserService;

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
            @CookieValue(value = "refreshToken", required = false) String refreshToken
    ) {
        if (refreshToken == null) {
            return ResponseEntity.status(401).build();
        }

        return authService.refresh(refreshToken);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        return ResponseEntity.ok(
                new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail()
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUser(@RequestBody UpdateUserDto dto) {

        UserDto updatedUser = currentUserService.UpdateUser(dto);
        return ResponseEntity.ok(updatedUser);
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
