package com.fitgeek.IATestPreparator.security;

import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.entities.enums.Role;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-expiration-ms}")
    private long accessExpirationMs;

    @Getter
    @Value("${jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String generateAccessToken(User user) {
        return buildToken(user.getEmail(), user.getRole(), accessExpirationMs);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user.getEmail(), user.getRole(), refreshExpirationMs);
    }

    private String buildToken(String email, Role role, long expirationMs) {
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }


    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public long getAccessExpirationInSeconds() {
        Duration d = Duration.ofMillis(accessExpirationMs);
        return d.getSeconds();
    }

}
