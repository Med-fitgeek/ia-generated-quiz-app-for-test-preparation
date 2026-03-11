package com.fitgeek.IATestPreparator.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "quiz_answers",
        indexes = {
                @Index(name = "idx_answer_session", columnList = "session_id"),
                @Index(name = "idx_answer_question", columnList = "question_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuizAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private QuizSession session;

    private int selectedIndex;

    private boolean correct;
}
