package com.fitgeek.memorix.repositories;

import com.fitgeek.memorix.entities.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    Page<Quiz> findAllByOwnerId(Long ownerId, Pageable pageable);
    Optional<Quiz> findByIdAndOwnerId(Long id, Long ownerId);
    int deleteByIdAndOwnerId(Long id, Long quizId);

}
