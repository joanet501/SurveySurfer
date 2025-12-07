package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.model.Survey;

import java.util.List;
import java.util.Optional;

public interface SurveyService {

    Survey createSurvey(Survey survey); // later we’ll use DTOs

    Optional<Survey> findById(Long id);

    Optional<Survey> findBySlug(String slug);

    Optional<Survey> findWithQuestionsBySlug(String slug);

    List<Survey> findAll();

    void deleteSurvey(Long id);
}