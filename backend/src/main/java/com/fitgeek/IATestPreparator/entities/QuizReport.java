package com.fitgeek.IATestPreparator.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "quiz_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuizReport {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession session;

    @Column(nullable = false)
    private double rate;

    @Column(nullable = false)
    private int correctAnswers;

    @Column(nullable = false)
    private int totalQuestions;

    @Column(length = 2000)
    private String recommendations;
}

