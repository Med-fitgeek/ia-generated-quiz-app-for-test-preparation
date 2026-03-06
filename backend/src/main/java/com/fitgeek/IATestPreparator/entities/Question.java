package com.fitgeek.IATestPreparator.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Question {
    @Id
    @GeneratedValue
    private Long id;

    private String statement;

    @ElementCollection
    private List<String> choices;

    private int correctIndex;

    @Column(length = 2000)
    private String explanation;

    @Column(length = 2000)
    private String sourceQuote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

}
