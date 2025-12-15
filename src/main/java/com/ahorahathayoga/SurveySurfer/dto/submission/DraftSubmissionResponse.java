package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DraftSubmissionResponse {
    private String submissionId;
    private LocalDateTime savedAt;
}
