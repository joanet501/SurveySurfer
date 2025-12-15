package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionDetailResponse {
    private String id;
    private String status;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;
    private Integer durationSeconds;
    private String variant;
    private String region;
    private String device;
    private Map<String, Object> answers;
}
