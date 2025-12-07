package com.ahorahathayoga.SurveySurfer.dto.survey;

import lombok.Getter;

import java.util.Map;

@Getter
public class SurveyValidationResult {

    private final boolean valid;
    /**
     * Map<questionId, errorMessage>
     */
    private final Map<Long, String> errors;

    private SurveyValidationResult(boolean valid, Map<Long, String> errors) {
        this.valid = valid;
        this.errors = errors;
    }

    public static SurveyValidationResult ok() {
        return new SurveyValidationResult(true, Map.of());
    }

    public static SurveyValidationResult withErrors(Map<Long, String> errors) {
        return new SurveyValidationResult(false, errors);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }
}