package com.ahorahathayoga.SurveySurfer.dto.stats;

import lombok.*;
import java.util.List;
import java.util.Map;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SurveyStatsDto {
    private Long surveyId;
    private String title;
    private int totalResponses;
    private List<QuestionStatsDto> questionStats;

    @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
    public static class QuestionStatsDto {
        private Long questionId;
        private String questionText;
        private String type;

        // For SCALE/NUMERIC
        private Double average;

        // For RADIO/MULTIPLE/SELECT (Option -> Count)
        private Map<String, Integer> optionCounts;

        // For OPEN_TEXT (just the last few responses as examples)
        private List<String> recentAnswers;
    }
}