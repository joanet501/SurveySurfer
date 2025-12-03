package com.ahorahathayoga.SurveySurfer.dto;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyViewDto {

    private Long id;
    private String title;
    private String description;
    private String slug;

    private List<QuestionViewDto> questions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionViewDto {
        private Long id;
        private String text;
        private QuestionType type;
        private boolean required;
        private Integer displayOrder;

        /**
         * Parsed options for RADIO / MULTIPLE, etc.
         * e.g. ["Sí","No","Tal vez"]
         */
        private List<String> options;

        /**
         * For SCALE questions, you might later extend with min, max, labels, etc.
         */
    }
}