package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmissionSummaryDto {
    private Long id;
    private LocalDateTime submittedAt;
    private String ipAddress;
    private int answerCount; // Useful to show if the user skipped questions
}