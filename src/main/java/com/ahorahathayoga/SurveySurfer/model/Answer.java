package com.ahorahathayoga.SurveySurfer.model;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "answers")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private Response response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String value;
    // Ejemplos según tipo:
    // RADIO → "Sí"
    // MULTIPLE → ["Sí","Tal vez"] (se guarda como JSON string)
    // NUMERIC → "4.5"
    // OPEN_TEXT → "Me gustó mucho el servicio..."
}