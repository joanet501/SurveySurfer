package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.dto.stats.SurveyStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionDetailsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionSummaryDto;
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
import java.util.*;
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

    @Override
    public SubmissionDetailsDto getSubmissionDetails(Long surveyId, Long submissionId) {
        // 1. Find the response and ensure it belongs to the specified survey
        Response response = responseRepository.findById(submissionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Submission not found"));

        if (!response.getSurvey().getId().equals(surveyId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submission does not belong to this survey");
        }

        // 2. Map to DTO
        List<SubmissionDetailsDto.AnswerDetailDto> answerDtos = response.getAnswers().stream()
                .map(a -> SubmissionDetailsDto.AnswerDetailDto.builder()
                        .questionId(a.getQuestion().getId())
                        .questionText(a.getQuestion().getText())
                        .questionType(a.getQuestion().getType().name())
                        .value(a.getValue())
                        .build())
                .toList();

        return SubmissionDetailsDto.builder()
                .id(response.getId())
                .surveyId(response.getSurvey().getId())
                .surveyTitle(response.getSurvey().getTitle())
                .submittedAt(response.getSubmittedAt())
                .ipAddress(response.getIpAddress())
                .answers(answerDtos)
                .build();
    }

    @Override
    public Page<SubmissionSummaryDto> getSurveySubmissions(Long surveyId, org.springframework.data.domain.Pageable pageable) {
        // We need a method in ResponseRepository to find by Survey ID
        return responseRepository.findBySurveyIdOrderBySubmittedAtDesc(surveyId, pageable)
                .map(response -> SubmissionSummaryDto.builder()
                        .id(response.getId())
                        .submittedAt(response.getSubmittedAt())
                        .ipAddress(response.getIpAddress())
                        .answerCount(response.getAnswers().size())
                        .build());
    }


    @Override
    public SurveyStatsDto getSurveyStats(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        List<Response> responses = responseRepository.findBySurveyId(surveyId);
        int totalResponses = responses.size();

        List<SurveyStatsDto.QuestionStatsDto> qStatsList = survey.getQuestions().stream().map(question -> {
            var statsBuilder = SurveyStatsDto.QuestionStatsDto.builder()
                    .questionId(question.getId())
                    .questionText(question.getText())
                    .type(question.getType().name());

            // Filter all answers for THIS specific question
            List<String> values = responses.stream()
                    .flatMap(r -> r.getAnswers().stream())
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .map(Answer::getValue)
                    .toList();

            switch (question.getType()) {
                case SCALE:
                    double avg = values.stream()
                            .mapToDouble(Double::parseDouble)
                            .average().orElse(0.0);
                    statsBuilder.average(avg);
                    break;

                case RADIO:
                    Map<String, Integer> counts = new HashMap<>();
                    values.forEach(v -> counts.merge(v, 1, Integer::sum));
                    statsBuilder.optionCounts(counts);
                    break;

                case MULTIPLE:
                    // Multiple values are stored as JSON strings like ["Opt1", "Opt2"]
                    Map<String, Integer> mCounts = new HashMap<>();
                    values.forEach(v -> {
                        try {
                            // Simple way to parse ["A","B"] without full Jackson overhead for this example
                            String cleaned = v.replace("[", "").replace("]", "").replace("\"", "");
                            for (String part : cleaned.split(",")) {
                                if (!part.trim().isEmpty()) mCounts.merge(part.trim(), 1, Integer::sum);
                            }
                        } catch (Exception e) { /* ignore malformed */ }
                    });
                    statsBuilder.optionCounts(mCounts);
                    break;

                case OPEN_TEXT:
                    statsBuilder.recentAnswers(values.stream().limit(5).toList());
                    break;
            }

            return statsBuilder.build();
        }).toList();

        return SurveyStatsDto.builder()
                .surveyId(survey.getId())
                .title(survey.getTitle())
                .totalResponses(totalResponses)
                .questionStats(qStatsList)
                .build();
    }
}

