package com.ahorahathayoga.SurveySurfer.controller.web;

import com.ahorahathayoga.SurveySurfer.dto.QuestionStatsDto;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.service.ResponseService;
import com.ahorahathayoga.SurveySurfer.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/surveys")
@RequiredArgsConstructor
public class AdminSurveyDashboardController {

    private final SurveyService surveyService;
    private final ResponseService responseService;

    @GetMapping("/{id}/dashboard")
    public String surveyDashboard(@PathVariable Long id, Model model) {
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        long totalResponses = responseService.countResponsesForSurvey(survey);
        List<QuestionStatsDto> questionStats = responseService.getQuestionStatsForSurvey(survey);

        model.addAttribute("survey", survey);
        model.addAttribute("totalResponses", totalResponses);
        model.addAttribute("questionStats", questionStats);

        return "admin/survey-dashboard";
    }
}