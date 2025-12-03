package com.ahorahathayoga.SurveySurfer.repository;

import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    // Like: Question::where('survey_id', $surveyId)->orderBy('display_order')->get()
    List<Question> findBySurveyOrderByDisplayOrderAscIdAsc(Survey survey);
}
