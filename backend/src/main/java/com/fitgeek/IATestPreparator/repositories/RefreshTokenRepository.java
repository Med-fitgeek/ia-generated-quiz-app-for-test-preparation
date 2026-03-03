package com.fitgeek.IATestPreparator.repositories;

import com.fitgeek.IATestPreparator.entities.RefreshToken;
import com.fitgeek.IATestPreparator.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String refreshToken);

    void deleteByUser(User user);
}
