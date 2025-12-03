package com.ahorahathayoga.SurveySurfer.repository;

import com.ahorahathayoga.SurveySurfer.model.Answer;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // All answers for a given question (used in stats)
    List<Answer> findByQuestion(Question question);

    // Example: for numeric averages, etc.
    @Query("""  
           SELECT a.value   
           FROM Answer a   
           WHERE a.question = :question  
           """)
    List<String> findAllValuesByQuestion(Question question);

    // Count answers per survey (joins through response.survey)
    @Query("""  
           SELECT COUNT(a)  
           FROM Answer a  
           WHERE a.response.survey = :survey  
           """)
    long countBySurvey(Survey survey);
}