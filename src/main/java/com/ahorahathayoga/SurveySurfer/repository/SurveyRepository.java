package com.ahorahathayoga.SurveySurfer.repository;

import com.ahorahathayoga.SurveySurfer.model.Survey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    // Equivalent to: Survey::where('slug', $slug)->first()
    Optional<Survey> findBySlug(String slug);

    // Sometimes, for the form we want survey + ordered questions in a single shot:
    @EntityGraph(attributePaths = "questions")
    Optional<Survey> findWithQuestionsBySlug(String slug);
}
