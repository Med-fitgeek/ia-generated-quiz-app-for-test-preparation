package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.LoginRequestDto;
import com.fitgeek.IATestPreparator.dtos.LoginResponseDto;
import com.fitgeek.IATestPreparator.dtos.RegisterRequestDto;
import com.fitgeek.IATestPreparator.dtos.RegisterResponseDto;
import com.fitgeek.IATestPreparator.entities.RefreshToken;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Role;
import com.fitgeek.IATestPreparator.repositories.RefreshTokenRepository;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.security.JwtUtil;
import com.fitgeek.IATestPreparator.services.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
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
    public RegisterResponseDto register(RegisterRequestDto registerRequestDto) {

        if (userRepository.existsByEmail(registerRequestDto.email()))
            throw new IllegalStateException ("Email already exists");

        if (userRepository.existsByUsername(registerRequestDto.username()))
            throw new IllegalStateException ("Username already exists");

        User user = User.builder()
                .username(registerRequestDto.username())
                .email(registerRequestDto.email())
                .password(passwordEncoder.encode(registerRequestDto.password()))
                .role(Role.USER)
                .build();

        User savedUser = userRepository.save(user);

        return new RegisterResponseDto(savedUser.getId(), savedUser.getUsername());
    }

    @Override
    public LoginResponseDto login(LoginRequestDto dto) {

        User user = userRepository.findByEmail(dto.email())
                .orElseThrow(() -> new IllegalStateException ("Invalid credentials"));

        if (!passwordEncoder.matches(dto.password(), user.getPassword())) {
            throw new IllegalStateException ("Invalid credentials");
        }

        // 1. Access token
        String accessToken = jwtUtil.generateAccessToken(user);

        // 2. Refresh token
        String refreshToken = jwtUtil.generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .token(refreshToken)
                .expiryDate(new Date(System.currentTimeMillis() + jwtUtil.getRefreshExpirationMs()))
                .user(user)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);


        return new LoginResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                jwtUtil.getAccessExpirationInSeconds()
        );
    }

    @Override
    public LoginResponseDto refreshAccessToken(String refreshToken) {

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalStateException("Invalid refresh token");
        }

        // 2. Récupérer le refresh token côté serveur
        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalStateException("Refresh token not found"));

        if (storedToken.getExpiryDate().before(new Date())) {
            throw new IllegalStateException("Refresh token expired");
        }

        User user = storedToken.getUser();
        String newAccessToken = jwtUtil.generateAccessToken(user);

        // 5. Retourner le nouveau token
        return new LoginResponseDto(
                newAccessToken,
                refreshToken, // on peut renvoyer le même refresh token
                "Bearer",
                jwtUtil.getAccessExpirationInSeconds()
        );
    }
    
}
