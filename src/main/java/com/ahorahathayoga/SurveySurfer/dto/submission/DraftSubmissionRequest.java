package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DraftSubmissionRequest {
    private String submissionId;
    private Map<String, Object> answers;
    private Integer currentStep;
    private Integer durationSeconds;
}
