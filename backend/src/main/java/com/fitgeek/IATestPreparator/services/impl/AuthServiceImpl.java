package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.LoginRequestDto;
import com.fitgeek.IATestPreparator.dtos.LoginResponseDto;
import com.fitgeek.IATestPreparator.dtos.RegisterRequestDto;
import com.fitgeek.IATestPreparator.entities.RefreshToken;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Role;
import com.fitgeek.IATestPreparator.repositories.RefreshTokenRepository;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.security.JwtUtil;
import com.fitgeek.IATestPreparator.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public ResponseEntity<LoginResponseDto> register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.email()))
            throw new IllegalStateException("Email already exists");

        if (userRepository.existsByUsername(dto.username()))
            throw new IllegalStateException("Username already exists");

        User user = User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(passwordEncoder.encode(dto.password()))
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtUtil.getRefreshExpirationMs()))
                .user(user)
                .build();

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(entity);

        // 🍪 Cookie HttpOnly
        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // true en prod HTTPS
                .path("/")
                .maxAge(jwtUtil.getRefreshExpirationMs() / 1000)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponseDto(
                        accessToken,
                        "Bearer",
                        jwtUtil.getAccessExpirationInSeconds()
                ));
    }

    @Override
    public ResponseEntity<LoginResponseDto> login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalStateException ("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalStateException ("Invalid credentials");
        }

        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtUtil.getRefreshExpirationMs()))
                .user(user)
                .build();

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.save(entity);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(false) // true en prod
                .path("/")
                .maxAge(jwtUtil.getRefreshExpirationMs() / 1000)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new LoginResponseDto(
                        accessToken,
                        "Bearer",
                        jwtUtil.getAccessExpirationInSeconds()
                ));
    }

    @Override
    public ResponseEntity<LoginResponseDto> refresh(String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }

        RefreshToken stored = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalStateException("Refresh token not found"));

        if (stored.getExpiryDate().before(new Date())) {
            throw new IllegalStateException("Refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(stored.getUser());

        return ResponseEntity.ok(
                new LoginResponseDto(
                        newAccessToken,
                        "Bearer",
                        jwtUtil.getAccessExpirationInSeconds()
                )
        );
    }
    
}
