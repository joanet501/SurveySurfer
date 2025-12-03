package com.ahorahathayoga.SurveySurfer.dto;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyCreateUpdateDto {

    private String title;
    private String slug;
    private String description;

    private List<QuestionCreateUpdateDto> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionCreateUpdateDto {
        private Long id; // null for new; non-null for update
        private String text;
        private QuestionType type;
        private boolean required;
        private Integer displayOrder;
        /**
         * For RADIO/MULTIPLE options.
         */
        private List<String> options;
        /**
         * For SCALE advanced config later (min, max, labels…) – for now we’ll skip.
         */
    }
}