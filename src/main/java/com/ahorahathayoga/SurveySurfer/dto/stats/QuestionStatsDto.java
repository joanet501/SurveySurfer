package com.ahorahathayoga.SurveySurfer.dto.stats;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import lombok.*;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionStatsDto {

    private Long questionId;
    private String questionText;
    private QuestionType type;

    /**
     * For RADIO/MULTIPLE/YES_NO:
     *   option label -> count
     */
    private Map<String, Long> optionCounts;

    /**
     * For NUMERIC/SCALE:
     *   simple aggregate statistics
     */
    private Double average;
    private Double min;
    private Double max;

    private Long totalAnswers;

}