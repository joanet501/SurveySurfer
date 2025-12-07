package com.ahorahathayoga.SurveySurfer.dto.survey;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResponseDto {

    private Long id;
    private String username;
    private QuestionType questionType;
    private String title;
    private String slug;
    private String description;
    private LocalDateTime createdAt;

    private List<QuestionDto> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionDto {
        private Long id;
        private String text;
        private QuestionType type;
        private boolean required;
        private Integer displayOrder;
        private List<String> options;
    }
}