package com.fitgeek.memorix.repositories;

import com.fitgeek.memorix.entities.RefreshToken;
import com.fitgeek.memorix.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshToken);

    void deleteByUser(User user);
}
