package com.fitgeek.IATestPreparator.repositories;

import com.fitgeek.IATestPreparator.entities.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
}
