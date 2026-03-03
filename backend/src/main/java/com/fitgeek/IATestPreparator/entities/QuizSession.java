package com.fitgeek.IATestPreparator.entities;

import com.fitgeek.IATestPreparator.entities.enums.Difficulty;
import com.fitgeek.IATestPreparator.entities.enums.SessionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "quiz_sessions")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class QuizSession {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SessionStatus status; // IN_PROGRESS / COMPLETED

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizAnswer> answers;

    private Integer correctCount;
    private Integer totalQuestions;
    private Long scorePercentage;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;

    private Long durationInSeconds;

    @PrePersist
    public void onCreate(){
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
}
