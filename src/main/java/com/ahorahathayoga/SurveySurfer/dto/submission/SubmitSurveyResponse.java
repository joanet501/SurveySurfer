package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitSurveyResponse {
    private String id;
    private String status;
    private LocalDateTime submittedAt;
}
