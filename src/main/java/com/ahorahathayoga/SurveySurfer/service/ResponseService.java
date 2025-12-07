package com.ahorahathayoga.SurveySurfer.service;

import com.ahorahathayoga.SurveySurfer.dto.stats.QuestionStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyValidationResult;
import com.ahorahathayoga.SurveySurfer.model.Response;
import com.ahorahathayoga.SurveySurfer.model.Survey;

import java.util.List;
import java.util.Map;

public interface ResponseService {

    /**
     * Saves a response for a survey.
     *
     * @param survey    The survey being responded to.
     * @param answersMap Map<questionId, rawValue> where:
     *                   - RADIO/YES_NO/EMAIL/DATE/NUMERIC: single string
     *                   - MULTIPLE: comma-separated string or JSON array (we’ll decide)
     * @param ipAddress Request IP
     * @param userAgent User-Agent header
     * @param sessionId Session or UUID
     * @return Persisted Response entity
     */
    Response saveResponse(
            Survey survey,
            Map<Long, String> answersMap,
            String ipAddress,
            String userAgent,
            String sessionId
    );
    List<QuestionStatsDto> getQuestionStatsForSurvey(Survey survey);
    /**
     * Very simple high-level stats (for dashboard later we’ll create richer DTOs).
     */
    long countResponsesForSurvey(Survey survey);

    /**
     * Validate answers for a survey, by question definition.
     */
    SurveyValidationResult validateAnswers(Survey survey, Map<Long, String> answersMap);
}