package com.fitgeek.IATestPreparator.entities;

import jakarta.annotation.PreDestroy;
import jakarta.persistence.*;
import lombok.*;

import org.aspectj.weaver.patterns.TypePatternQuestions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    private String sourceChecksum;
    private String generatorVersion;
    private LocalDateTime generatedAt;
    private Long numberOfSessions;

    @Builder.Default
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions = new ArrayList<>();


    public void addQuestion(Question question) {
        questions.add(question);
        question.setQuiz(this);
    }

    @PreDestroy
    public void onCreate() {
        this.generatedAt = LocalDateTime.now();
    }
}

