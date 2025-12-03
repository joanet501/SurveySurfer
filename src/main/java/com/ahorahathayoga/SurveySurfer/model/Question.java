package com.ahorahathayoga.SurveySurfer.model;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "questions")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonIgnore  // Evita ciclos infinitos al serializar JSON
    private Survey survey;

    @Column(nullable = false, length = 500)
    private String text;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type = QuestionType.OPEN_TEXT;

    @Column(columnDefinition = "TEXT")
    private String optionsJson;
    // Ejemplos:
    // RADIO/MULTIPLE: ["Sí","No","Tal vez"]
    // SCALE: {"min":1,"max":5,"labels":["Muy malo","Excelente"]}

    private Integer displayOrder = 0; // Para ordenar en el formulario

    private boolean required = true;
}