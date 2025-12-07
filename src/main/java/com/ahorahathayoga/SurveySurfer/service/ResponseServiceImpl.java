package com.ahorahathayoga.SurveySurfer.service;

import com.ahorahathayoga.SurveySurfer.dto.stats.QuestionStatsDto;
import com.ahorahathayoga.SurveySurfer.dto.survey.SurveyValidationResult;
import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.model.Answer;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Response;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.repository.AnswerRepository;
import com.ahorahathayoga.SurveySurfer.repository.QuestionRepository;
import com.ahorahathayoga.SurveySurfer.repository.ResponseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    @Override
    @Transactional
    public Response saveResponse(
            Survey survey,
            Map<Long, String> answersMap,
            String ipAddress,
            String userAgent,
            String sessionId
    ) {
        // We assume validation already performed by controller.
        Response response = Response.builder()
                .survey(survey)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .sessionId(sessionId)
                .build();

        response = responseRepository.save(response);

        for (Map.Entry<Long, String> entry : answersMap.entrySet()) {
            Long questionId = entry.getKey();
            String rawValue = entry.getValue();

            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid question id: " + questionId));

            String storedValue = normalizeAnswerValue(question, rawValue);

            Answer answer = Answer.builder()
                    .response(response)
                    .question(question)
                    .value(storedValue)
                    .build();

            answerRepository.save(answer);
            response.addAnswer(answer);
        }

        return response;
    }

    @Override
    public long countResponsesForSurvey(Survey survey) {
        return responseRepository.countBySurvey(survey);
    }

    @Override
    public SurveyValidationResult validateAnswers(Survey survey, Map<Long, String> answersMap) {
        Map<Long, String> errors = new HashMap<>();

        survey.getQuestions().forEach(q -> {
            Long qId = q.getId();
            String raw = answersMap != null ? answersMap.get(qId) : null;
            String value = raw != null ? raw.trim() : "";

            // Required check
            if (q.isRequired() && (value.isEmpty())) {
                errors.put(qId, "Este campo es obligatorio.");
                return;
            }

            // If not required and empty, skip type-specific checks
            if (!q.isRequired() && value.isEmpty()) {
                return;
            }

            // Type-specific validation
            switch (q.getType()) {
                case NUMERIC:
                case SCALE:
                    try {
                        Double.parseDouble(value);
                    } catch (NumberFormatException e) {
                        errors.put(qId, "Debe ser un número válido.");
                    }
                    break;
                case EMAIL:
                    if (!EMAIL_PATTERN.matcher(value).matches()) {
                        errors.put(qId, "Debe ser un correo electrónico válido.");
                    }
                    break;
                case DATE:
                    // Basic yyyy-MM-dd format check (browser will typically enforce too)
                    if (!value.matches("\\d{4}-\\d{2}-\\d{2}")) {
                        errors.put(qId, "Debe ser una fecha válida (YYYY-MM-DD).");
                    }
                    break;
                case RADIO:
                case MULTIPLE:
                case YES_NO:
                case OPEN_TEXT:
                default:
                    // No extra checks for now
                    break;
            }
        });

        if (errors.isEmpty()) {
            return SurveyValidationResult.ok();
        }
        return SurveyValidationResult.withErrors(errors);
    }

    private String normalizeAnswerValue(Question question, String rawValue) {
        if (rawValue == null) {
            return "";
        }
        String trimmed = rawValue.trim();

        // For MULTIPLE (checkbox), you might get "a,b,c" from the JS helper.
        // If you want to store JSON instead:
        if (question.getType() == QuestionType.MULTIPLE && !trimmed.isEmpty()) {
            // Convert comma-separated to JSON array: ["a","b","c"]
            String[] parts = trimmed.split(",");
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < parts.length; i++) {
                if (i > 0) sb.append(",");
                sb.append("\"").append(parts[i].replace("\"", "\\\"")).append("\"");
            }
            sb.append("]");
            return sb.toString();
        }

        return trimmed;
    }
    @Override
    public List<QuestionStatsDto> getQuestionStatsForSurvey(Survey survey) {
        return survey.getQuestions().stream()
                .map(this::computeStatsForQuestion)
                .toList();
    }

    private QuestionStatsDto computeStatsForQuestion(Question question) {
        var answers = answerRepository.findByQuestion(question);
        long total = answers.size();

        QuestionType type = question.getType();

        QuestionStatsDto.QuestionStatsDtoBuilder builder = QuestionStatsDto.builder()
                .questionId(question.getId())
                .questionText(question.getText())
                .type(type)
                .totalAnswers(total);

        if (total == 0) {
            return builder.build();
        }

        switch (type) {
            case RADIO, YES_NO, MULTIPLE -> {
                Map<String, Long> counts = new java.util.HashMap<>();
                for (var ans : answers) {
                    String value = ans.getValue();
                    if (value == null || value.isBlank()) continue;

                    if (type == QuestionType.MULTIPLE) {
                        // stored as JSON array or comma separated; we stored JSON array in normalizeAnswerValue
                        // quick & dirty split by " or use JSON parser; here simple approach:
                        if (value.startsWith("[") && value.endsWith("]")) {
                            // remove [ and ]
                            String inner = value.substring(1, value.length() - 1);
                            // split by ",", handle quotes
                            for (String token : inner.split(",")) {
                                String opt = token.trim().replaceAll("^\"|\"$", "");
                                if (opt.isEmpty()) continue;
                                counts.merge(opt, 1L, Long::sum);
                            }
                        } else {
                            // Fallback: comma-separated
                            for (String opt : value.split(",")) {
                                String v = opt.trim();
                                if (!v.isEmpty()) {
                                    counts.merge(v, 1L, Long::sum);
                                }
                            }
                        }
                    } else {
                        counts.merge(value, 1L, Long::sum);
                    }
                }
                builder.optionCounts(counts);
            }
            case NUMERIC, SCALE -> {
                double sum = 0.0;
                double min = Double.MAX_VALUE;
                double max = -Double.MAX_VALUE;
                int count = 0;
                for (var ans : answers) {
                    String v = ans.getValue();
                    if (v == null || v.isBlank()) continue;
                    try {
                        double d = Double.parseDouble(v);
                        sum += d;
                        min = Math.min(min, d);
                        max = Math.max(max, d);
                        count++;
                    } catch (NumberFormatException ignored) {
                    }
                }
                if (count > 0) {
                    builder.average(sum / count)
                            .min(min)
                            .max(max);
                }
            }
            case OPEN_TEXT, EMAIL, DATE -> {
                // For now we don't aggregate; maybe later we do word clouds, etc.
            }
        }

        return builder.build();
    }

}