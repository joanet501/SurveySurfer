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
public class SubmissionListResponse {
    private List<SubmissionItemDto> items;
    private int page;
    private int size;
    private long totalItems;
    private int totalPages;
}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class SubmissionItemDto {
    private String id;
    private String status;
    private LocalDateTime submittedAt;
    private Integer durationSeconds;
    private String region;
    private String device;
}
