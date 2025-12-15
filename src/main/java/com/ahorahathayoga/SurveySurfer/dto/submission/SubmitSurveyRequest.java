package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.util.Map;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SubmitSurveyRequest {
    private String submissionId;
    private Map<String, Object> answers;
    private Integer durationSeconds;
    private String userAgent;
    private String fingerprint;
    private String region;
    private String variant;
}
