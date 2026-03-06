package com.fitgeek.IATestPreparator.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answer_explanations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AnswerExplanation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private QuizAnswer userAnswer;

    @Column(nullable = false, length = 1500)
    private String explanation;

    @Column(length = 1000)
    private String reference;
}

