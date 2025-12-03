package com.ahorahathayoga.SurveySurfer.model;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "responses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    @Column(updatable = false)
    private LocalDateTime submittedAt = LocalDateTime.now();

    // Opcional: identificar origen
    private String ipAddress;
    private String userAgent;
    private String sessionId; // UUID generado en el frontend o backend

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default  // <<< IMPORTANT
    private List<Answer> answers = new ArrayList<>();

    // Helper
    public void addAnswer(Answer answer) {
        if (answers == null) {              // safety guard
            answers = new ArrayList<>();
        }
        answers.add(answer);
        answer.setResponse(this);
    }
}