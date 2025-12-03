package com.ahorahathayoga.SurveySurfer.service;

import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor // like Laravel constructor dependency injection
public class SurveyServiceImpl implements SurveyService {

    private final SurveyRepository surveyRepository;

    @Override
    public Survey createSurvey(Survey survey) {
        // later we might generate slug if null, validate, etc.
        return surveyRepository.save(survey);
    }

    @Override
    public Optional<Survey> findById(Long id) {
        return surveyRepository.findById(id);
    }

    @Override
    public Optional<Survey> findBySlug(String slug) {
        return surveyRepository.findBySlug(slug);
    }

    @Override
    public Optional<Survey> findWithQuestionsBySlug(String slug) {
        return surveyRepository.findWithQuestionsBySlug(slug);
    }

    @Override
    public List<Survey> findAll() {
        return surveyRepository.findAll();
    }

    @Override
    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }
}