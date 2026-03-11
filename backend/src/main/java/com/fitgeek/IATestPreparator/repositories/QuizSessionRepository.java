package com.fitgeek.IATestPreparator.repositories;

import com.fitgeek.IATestPreparator.entities.QuizSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizSessionRepository extends JpaRepository<QuizSession, Long> {

    @Query("""
    SELECT s FROM QuizSession s
    WHERE s.user.id = :userId
    AND s.quiz.id = :quizId
    AND s.status IN ('CREATED','STARTED')
""")
    Optional<QuizSession> findActiveSession(Long userId, Long quizId);

    Optional<QuizSession> findByIdAndUserId(Long id, Long userId);

    Page<QuizSession> findAllByUserId(Long id, Pageable pageable);

    int deleteByIdAndUserId(Long sessionId, Long userId);
}
