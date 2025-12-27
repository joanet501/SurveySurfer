package com.ahorahathayoga.SurveySurfer.dto.submission;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SurveySubmissionDto {
    private List<AnswerSubmissionDto> answers;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class AnswerSubmissionDto {
        private Long questionId;
        private String value; // This will be the JSON string for multiple choice or plain text
    }
}