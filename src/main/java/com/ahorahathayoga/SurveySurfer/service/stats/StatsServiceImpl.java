package com.ahorahathayoga.SurveySurfer.service.stats;

import com.ahorahathayoga.SurveySurfer.dto.stats.QuestionStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.stats.SurveyStatsDto;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.repository.QuestionRepository;
import com.ahorahathayoga.SurveySurfer.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl
{
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;


    public SurveyStatsDto  getSurveyStats(Integer id)
    {



    }

    public List<QuestionStatsDto> getQuestionStats(Survey survey)
    {
        List<Question>  questions = survey.getQuestions();
        int totalQuestions = questions.size();
        List<QuestionStatsDto> questionStatsDtos = new ArrayList<>();
        Double average;
        Double min;
        Double max;
        Long totalAnswers;

        for(Question question : questions){
            totalAnswers = question.getTotalAnswers();



            QuestionStatsDto questionStatsDto = QuestionStatsDto.builder()
                    .questionId(question.getId())
                    .questionText(question.getText())
                    .questionType()

        }



    }

}
