package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SubmissionDetailsDto {
    private Long id;
    private Long surveyId;
    private String surveyTitle;
    private LocalDateTime submittedAt;
    private String ipAddress;
    private List<AnswerDetailDto> answers;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AnswerDetailDto {
        private Long questionId;
        private String questionText;
        private String questionType;
        private String value;
    }
}