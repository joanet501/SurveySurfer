package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.dto.submission.SurveySubmissionDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.enums.SurveyStatus;
import com.ahorahathayoga.SurveySurfer.model.*;
import com.ahorahathayoga.SurveySurfer.repository.ResponseRepository;
import com.ahorahathayoga.SurveySurfer.repository.SurveyRepository;
import com.ahorahathayoga.SurveySurfer.util.SurveyMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor // like Laravel constructor dependency injection
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository surveyRepository;
    private final ResponseRepository responseRepository;
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

    @Override
    public Survey updateStatus(Long id, com.ahorahathayoga.SurveySurfer.enums.SurveyStatus status) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Survey not found"));

        survey.setStatus(status);
        return surveyRepository.save(survey);
    }

    @Override
    public void submitResponse(Long surveyId, SurveySubmissionDto submissionDto, String ipAddress, String userAgent) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        // 1. Only allow submissions for PUBLISHED surveys
        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "This survey is not accepting responses yet.");
        }

        // 2. Create the Response header
        Response response = Response.builder()
                .survey(survey)
                .submittedAt(LocalDateTime.now())
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        // 3. Map DTO answers to Entity answers
        for (var answerDto : submissionDto.getAnswers()) {
            Question question = survey.getQuestions().stream()
                    .filter(q -> q.getId().equals(answerDto.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Question ID " + answerDto.getQuestionId() + " does not belong to this survey"));

            // Basic validation: check if required
            if (question.isRequired() && (answerDto.getValue() == null || answerDto.getValue().isBlank())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Question '" + question.getText() + "' is required");
            }

            Answer answer = Answer.builder()
                    .question(question)
                    .value(answerDto.getValue())
                    .build();

            response.addAnswer(answer);
        }

        // 4. Save (CascadeType.ALL in Response model will save the answers automatically)
        // You will need a ResponseRepository for this
        responseRepository.save(response);
    }


}

