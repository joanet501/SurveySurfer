package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.SurveyRepository;
import com.ahorahathayoga.SurveySurfer.util.SurveyMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
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
    public Page<SurveyViewDto> findAll(Pageable pageable) {
        Page<Survey> surveys = surveyRepository.findAll(pageable);
        return surveys.map(SurveyMapper::toSurveyViewDto);
    }

    @Override
    public void deleteSurvey(Long id) {
        surveyRepository.deleteById(id);
    }



}

