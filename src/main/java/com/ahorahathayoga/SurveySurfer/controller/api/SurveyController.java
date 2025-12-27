package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.dto.stats.SurveyStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionDetailsDto;
import com.ahorahathayoga.SurveySurfer.dto.submission.SubmissionSummaryDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyCreateUpdateDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyResponseDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.service.survey.SurveyAuthorizationService;
import com.ahorahathayoga.SurveySurfer.service.survey.SurveyService;
import com.ahorahathayoga.SurveySurfer.util.SurveyApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final UserRepository userRepository;
    private final SurveyAuthorizationService surveyAuthorizationService;

    // GET /api/surveys
    @GetMapping
    public ResponseEntity<Page<SurveyViewDto>> listSurveys(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        if (page < 0 || size < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Page and size must be non-negative");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<SurveyViewDto> surveysPage = surveyService.findAll(pageable);
        return ResponseEntity.ok(surveysPage);
    }

    // GET /api/surveys/{id}
    @GetMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> getSurveyById(@PathVariable Long id) {
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUserOrNull();
        surveyAuthorizationService.checkCanView(survey, currentUser);

        SurveyResponseDto dto = SurveyApiMapper.toResponseDto(survey);
        return ResponseEntity.ok(dto);
    }

    // GET /api/surveys/slug/{slug}
    @GetMapping("/slug/{slug}")
    public ResponseEntity<SurveyResponseDto> getSurveyBySlug(@PathVariable String slug) {
        Survey survey = surveyService.findWithQuestionsBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUserOrNull();
        surveyAuthorizationService.checkCanView(survey, currentUser);

        SurveyResponseDto dto = SurveyApiMapper.toResponseDto(survey);
        return ResponseEntity.ok(dto);
    }

    // POST /api/surveys
    @PostMapping
    public ResponseEntity<SurveyResponseDto> createSurvey(@RequestBody SurveyCreateUpdateDto dto) {
        validateSurveyDto(dto);

        Survey survey = new Survey();
        SurveyApiMapper.applyCreateUpdateDtoToEntity(dto, survey);

        User currentUser = getCurrentUser();
        survey.setUser(currentUser);

        Survey saved = surveyService.createSurvey(survey);
        SurveyResponseDto responseDto = SurveyApiMapper.toResponseDto(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // PUT /api/surveys/{id}
   /* @PutMapping("/{id}")
    public ResponseEntity<SurveyResponseDto> updateSurvey(
            @PathVariable Long id,
            @RequestBody SurveyCreateUpdateDto dto) {

        validateSurveyDto(dto);

        Survey existing = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanEdit(existing, currentUser);

        SurveyApiMapper.applyCreateUpdateDtoToEntity(dto, existing);
        Survey saved = surveyService.updateSurvey(existing);

        SurveyResponseDto responseDto = SurveyApiMapper.toResponseDto(saved);
        return ResponseEntity.ok(responseDto);
    }
*/
    // DELETE /api/surveys/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSurvey(@PathVariable Long id) {
        Survey existing = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanDelete(existing, currentUser);

        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    // --- Helpers ---

    private void validateSurveyDto(SurveyCreateUpdateDto dto) {
        Map<String, String> errors = new HashMap<>();

        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            errors.put("title", "Title is required");
        }
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            errors.put("slug", "Slug is required");
        }
        if (dto.getQuestions() == null || dto.getQuestions().isEmpty()) {
            errors.put("questions", "At least one question is required");
        } else {
            int index = 0;
            for (var q : dto.getQuestions()) {
                String prefix = "questions[" + index + "]";
                if (q.getText() == null || q.getText().isBlank()) {
                    errors.put(prefix + ".text", "Question text is required");
                }
                if (q.getType() == null) {
                    errors.put(prefix + ".type", "Question type is required");
                }
                index++;
            }
        }

        if (!errors.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Validation failed: " + errors);
        }
    }

    private User getCurrentUser() {
        String username = getCurrentUsername();
        if (username == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }

    private User getCurrentUserOrNull() {
        String username = getCurrentUsername();
        if (username == null) return null;
        return userRepository.findByUsername(username).orElse(null);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return auth.getName();
    }

    @PatchMapping("/{id}/draft")
    public ResponseEntity<SurveyResponseDto> markAsDraft(@PathVariable Long id) {
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanEdit(survey, currentUser);

        Survey updated = surveyService.updateStatus(id, com.ahorahathayoga.SurveySurfer.enums.SurveyStatus.DRAFT);
        return ResponseEntity.ok(SurveyApiMapper.toResponseDto(updated));
    }

    // PATCH /api/surveys/{id}/publish
    @PatchMapping("/{id}/publish")
    public ResponseEntity<SurveyResponseDto> markAsPublished(@PathVariable Long id) {
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanEdit(survey, currentUser);

        // You could add extra validation here, e.g., ensuring the survey has questions before publishing
        if (survey.getQuestions().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cannot publish a survey with no questions");
        }

        Survey updated = surveyService.updateStatus(id, com.ahorahathayoga.SurveySurfer.enums.SurveyStatus.PUBLISHED);
        return ResponseEntity.ok(SurveyApiMapper.toResponseDto(updated));
    }


    // GET /api/surveys/{id}/submissions/{submissionId}
    @GetMapping("/{id}/submissions/{submissionId}")
    public ResponseEntity<SubmissionDetailsDto> getSubmission(
            @PathVariable Long id,
            @PathVariable Long submissionId) {

        // 1. Check if survey exists
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        // 2. Authorization: Only owner/admin can see submissions
        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanViewSubmissions(survey, currentUser);

        // 3. Get data
        SubmissionDetailsDto details = surveyService.getSubmissionDetails(id, submissionId);
        return ResponseEntity.ok(details);
    }

    // GET /api/surveys/{id}/submissions
    @GetMapping("/{id}/submissions")
    public ResponseEntity<Page<SubmissionSummaryDto>> listSubmissions(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        // Authorization: Only owner/admin
        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanViewSubmissions(survey, currentUser);

        Pageable pageable = PageRequest.of(page, size);
        Page<SubmissionSummaryDto> submissions = surveyService.getSurveySubmissions(id, pageable);

        return ResponseEntity.ok(submissions);
    }

    // GET /api/surveys/{id}/stats
    @GetMapping("/{id}/stats")
    public ResponseEntity<SurveyStatsDto> getStats(@PathVariable Long id) {
        Survey survey = surveyService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Survey not found"));

        User currentUser = getCurrentUser();
        surveyAuthorizationService.checkCanViewSubmissions(survey, currentUser);

        return ResponseEntity.ok(surveyService.getSurveyStats(id));
    }
}