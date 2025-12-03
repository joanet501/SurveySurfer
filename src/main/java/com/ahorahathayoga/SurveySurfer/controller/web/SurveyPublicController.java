package com.ahorahathayoga.SurveySurfer.controller.web;

import com.ahorahathayoga.SurveySurfer.dto.SurveyResponseForm;
import com.ahorahathayoga.SurveySurfer.dto.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.service.ResponseService;
import com.ahorahathayoga.SurveySurfer.service.SurveyService;
import com.ahorahathayoga.SurveySurfer.util.SurveyMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/s")
@RequiredArgsConstructor
public class SurveyPublicController {

    private final SurveyService surveyService;
    private final ResponseService responseService;

    /**
     * Show public survey form.
     * GET /s/{slug}
     */
    @GetMapping("/{slug}")
    public String showSurvey(
            @PathVariable String slug,
            Model model
    ) {
        Survey survey = surveyService.findWithQuestionsBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        SurveyViewDto surveyView = SurveyMapper.toSurveyViewDto(survey);

        model.addAttribute("survey", surveyView);
        model.addAttribute("responseForm", new SurveyResponseForm());

        return "survey/public-form"; // templates/survey/public-form.html
    }

    /**
     * Handle public survey submission.
     * POST /s/{slug}
     */
    @PostMapping("/{slug}")
    public String submitSurvey(
            @PathVariable String slug,
            @ModelAttribute("responseForm") SurveyResponseForm form,
            HttpServletRequest request,
            Model model
    ) {
        Survey survey = surveyService.findWithQuestionsBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Survey not found"));

        SurveyViewDto surveyView = SurveyMapper.toSurveyViewDto(survey);
        model.addAttribute("survey", surveyView);

        Map<Long, String> answersMap = form.getAnswers();

        var validationResult = responseService.validateAnswers(survey, answersMap);
        if (validationResult.hasErrors()) {
            // Re-show the form with errors
            model.addAttribute("errors", validationResult.getErrors());
            return "survey/public-form";
        }

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String sessionId = getOrCreateSessionId(request);

        responseService.saveResponse(survey, answersMap, ipAddress, userAgent, sessionId);

        model.addAttribute("surveyTitle", survey.getTitle());
        return "survey/thank-you";
    }

    private String getOrCreateSessionId(HttpServletRequest request) {
        var httpSession = request.getSession(true);
        String sessionId = (String) httpSession.getAttribute("surveySessionId");
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
            httpSession.setAttribute("surveySessionId", sessionId);
        }
        return sessionId;
    }
}