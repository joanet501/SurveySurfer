package com.ahorahathayoga.SurveySurfer.repository;

import com.ahorahathayoga.SurveySurfer.model.Response;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ResponseRepository extends JpaRepository<Response, Long> {
    long countBySurvey(Survey survey);

    List<Response> findBySurveyAndSubmittedAtBetween(
            Survey survey,
            LocalDateTime from,
            LocalDateTime to
    );

    // For dashboard stats, last N responses, etc.
    List<Response> findTop50BySurveyOrderBySubmittedAtDesc(Survey survey);
}
