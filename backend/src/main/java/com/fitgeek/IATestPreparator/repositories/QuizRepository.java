package com.fitgeek.IATestPreparator.repositories;

import com.fitgeek.IATestPreparator.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findAllByOwnerId(Long ownerId);
    Optional<Quiz> findByIdAndOwnerId(Long id, Long ownerId);
}
