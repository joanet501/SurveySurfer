package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.dto.stats.SurveyStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionDetailsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionSummaryDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SurveySubmissionDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SurveyService {

    Survey createSurvey(Survey survey); // later we’ll use DTOs

    Optional<Survey> findById(Long id);

    Optional<Survey> findBySlug(String slug);

    Optional<Survey> findWithQuestionsBySlug(String slug);

    Page<SurveyViewDto> findAll(Pageable pageable);

    void deleteSurvey(Long id);

    Survey updateStatus(Long id, com.ahorahathayoga.SurveySurfer.enums.SurveyStatus status);

    void submitResponse(Long surveyId, SurveySubmissionDto submissionDto, String ipAddress, String userAgent);

    SubmissionDetailsDto getSubmissionDetails(Long surveyId, Long submissionId);

    Page<SubmissionSummaryDto> getSurveySubmissions(Long surveyId, org.springframework.data.domain.Pageable pageable);

    SurveyStatsDto getSurveyStats(Long surveyId);
}