package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.dto.submission.SurveySubmissionDto;
import com.ahorahathayoga.SurveySurfer.service.survey.SurveyService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public/surveys")
@RequiredArgsConstructor
public class PublicSurveyController {

    private final SurveyService surveyService;

    @PostMapping("/{id}/submit")
    public ResponseEntity<Map<String, String>> submitSurvey(
            @PathVariable Long id,
            @RequestBody SurveySubmissionDto submissionDto,
            HttpServletRequest request) {

        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        surveyService.submitResponse(id, submissionDto, ipAddress, userAgent);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Survey submitted successfully");
        return ResponseEntity.ok(response);
    }
}