package com.fitgeek.IATestPreparator.entities;

import jakarta.persistence.*;
import lombok.*;
import org.aspectj.weaver.patterns.TypePatternQuestions;

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

    private String sourceChecksum;
    private String generatorVersion;

}

