package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsResponse {
    private Long totalResponses;
    private Long completedResponses;
    private Long draftResponses;
    private Double completionRate;
    private Double averageDuration;
    private LocalDateTime firstResponseAt;
    private LocalDateTime lastResponseAt;
    private List<Map<String, Object>> questionStats;
}
