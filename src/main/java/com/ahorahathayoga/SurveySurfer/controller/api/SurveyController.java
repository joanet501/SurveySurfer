package com.ahorahathayoga.SurveySurfer.controller.api;

import com.ahorahathayoga.SurveySurfer.dto.ApiErrorResponse;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyCreateUpdateDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyResponseDto;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.service.survey.SurveyService;
import com.ahorahathayoga.SurveySurfer.util.SurveyApiMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyController {

    private final SurveyService surveyService;
    private final UserRepository userRepository;


    // GET /api/surveys
    @GetMapping
    public List<SurveyResponseDto> listSurveys() {
        return surveyService.findAll().stream()
                .map(SurveyApiMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    // GET /api/surveys/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getSurveyById(@PathVariable Long id, HttpServletRequest request) {
        var optSurvey = surveyService.findById(id);
        if (optSurvey.isEmpty()) {
            return notFound("Survey not found", request.getRequestURI());
        }

        SurveyResponseDto dto = SurveyApiMapper.toResponseDto(optSurvey.get());
        return ResponseEntity.ok(dto);
    }

    // GET /api/surveys/slug/{slug}
    @GetMapping("/slug/{slug}")
    public ResponseEntity<?> getSurveyBySlug(@PathVariable String slug, HttpServletRequest request) {
        var optSurvey = surveyService.findWithQuestionsBySlug(slug);
        if (optSurvey.isEmpty()) {
            return notFound("Survey not found", request.getRequestURI());
        }

        SurveyResponseDto dto = SurveyApiMapper.toResponseDto(optSurvey.get());
        return ResponseEntity.ok(dto);
    }

    // POST /api/surveys
    @PostMapping
    public ResponseEntity<?> createSurvey( @RequestBody SurveyCreateUpdateDto dto, HttpServletRequest request) {
        // Basic validation similar to $request->validate()
        var errors = validateSurveyDto(dto);
        if (!errors.isEmpty()) {
            return badRequest("Validation failed", errors, request.getRequestURI());
        }
        Survey survey = new Survey();
        SurveyApiMapper.applyCreateUpdateDtoToEntity(dto, survey);

        String username = getCurrentUsername();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        survey.setUser(user);

        Survey saved = surveyService.createSurvey(survey);

        SurveyResponseDto responseDto = SurveyApiMapper.toResponseDto(saved);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    // PUT /api/surveys/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSurvey(
            @PathVariable Long id,
            @RequestBody SurveyCreateUpdateDto dto,
            HttpServletRequest request
    ) {
        var errors = validateSurveyDto(dto);
        if (!errors.isEmpty()) {
            return badRequest("Validation failed", errors, request.getRequestURI());
        }

        Survey existing = surveyService.findById(id)
                .orElse(null);
        if (existing == null) {
            return notFound("Survey not found", request.getRequestURI());
        }

        SurveyApiMapper.applyCreateUpdateDtoToEntity(dto, existing);
        Survey saved = surveyService.createSurvey(existing);

        SurveyResponseDto responseDto = SurveyApiMapper.toResponseDto(saved);
        return ResponseEntity.ok(responseDto);
    }

    // DELETE /api/surveys/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSurvey(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Survey existing = surveyService.findById(id)
                .orElse(null);
        if (existing == null) {
            return notFound("Survey not found", request.getRequestURI());
        }

        surveyService.deleteSurvey(id);
        return ResponseEntity.noContent().build();
    }

    // --- helpers ---

    private Map<String, String> validateSurveyDto(SurveyCreateUpdateDto dto) {
        java.util.Map<String, String> errors = new java.util.HashMap<>();
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            errors.put("title", "Title is required");
        }
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            errors.put("slug", "Slug is required");
        }
        // optionally: regex for slug, uniqueness check (slug exists, etc.)
        // Here we skip uniqueness due to complexity, but you could check via repo.

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

        return errors;
    }

    private ResponseEntity<ApiErrorResponse> notFound(String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    private ResponseEntity<ApiErrorResponse> badRequest(
            String message,
            Map<String, String> details,
            String path
    ) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(path)
                .details(Map.of("fieldErrors", details))
                .build();
        return ResponseEntity.badRequest().body(error);

    }

    //Helpers


    private String getCurrentUsername() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       if(auth == null || !auth.isAuthenticated()) {
           return null;
       }
       return auth.getPrincipal().toString();
    }

}