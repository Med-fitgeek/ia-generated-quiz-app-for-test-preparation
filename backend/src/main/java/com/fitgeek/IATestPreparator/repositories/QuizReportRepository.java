package com.fitgeek.IATestPreparator.repositories;

import com.fitgeek.IATestPreparator.entities.QuizReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuizReportRepository extends JpaRepository<QuizReport, Long> {

    Optional<QuizReport> findBySessionId(Long sessionId);
}
