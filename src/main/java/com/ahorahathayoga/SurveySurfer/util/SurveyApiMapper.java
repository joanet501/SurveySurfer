package com.ahorahathayoga.SurveySurfer.util;

import com.ahorahathayoga.SurveySurfer.dto.SurveyCreateUpdateDto;
import com.ahorahathayoga.SurveySurfer.dto.SurveyResponseDto;
import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyApiMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Entity -> Response DTO

    public static SurveyResponseDto toResponseDto(Survey survey) {
        return SurveyResponseDto.builder()
                .id(survey.getId())
                .username(survey.getUser().getUsername())
                .title(survey.getTitle())
                .slug(survey.getSlug())
                .description(survey.getDescription())
                .createdAt(survey.getCreatedAt())
                .questions(
                        survey.getQuestions().stream()
                                .sorted((q1, q2) -> {
                                    int d1 = q1.getDisplayOrder() == null ? 0 : q1.getDisplayOrder();
                                    int d2 = q2.getDisplayOrder() == null ? 0 : q2.getDisplayOrder();
                                    int cmp = Integer.compare(d1, d2);
                                    if (cmp == 0) {
                                        Long id1 = q1.getId() == null ? 0L : q1.getId();
                                        Long id2 = q2.getId() == null ? 0L : q2.getId();
                                        return Long.compare(id1, id2);
                                    }
                                    return cmp;
                                })
                                .map(SurveyApiMapper::toQuestionDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private static SurveyResponseDto.QuestionDto toQuestionDto(Question q) {
        return SurveyResponseDto.QuestionDto.builder()
                .id(q.getId())
                .text(q.getText())
                .type(q.getType())
                .required(q.isRequired())
                .displayOrder(q.getDisplayOrder())
                .options(parseOptions(q.getOptionsJson()))
                .build();
    }

    private static List<String> parseOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return OBJECT_MAPPER.readValue(optionsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    // Create/Update DTO -> Entity

    public static void applyCreateUpdateDtoToEntity(SurveyCreateUpdateDto dto, Survey survey) {
        survey.setTitle(dto.getTitle());
        survey.setSlug(dto.getSlug());
        survey.setDescription(dto.getDescription());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // For simplicity: on update, clear questions and recreate from DTO.
        // For production you might want finer-grained diff/merge.
        survey.getQuestions().clear();

        if (dto.getQuestions() != null) {
            int i = 0;
            for (SurveyCreateUpdateDto.QuestionCreateUpdateDto qDto : dto.getQuestions()) {
                Question q = new Question();
                q.setSurvey(survey);
                q.setText(qDto.getText());
                q.setType(qDto.getType() != null ? qDto.getType() : QuestionType.OPEN_TEXT);
                q.setRequired(qDto.isRequired());
                q.setDisplayOrder(qDto.getDisplayOrder() != null ? qDto.getDisplayOrder() : i);

                if (qDto.getOptions() != null && !qDto.getOptions().isEmpty()) {
                    try {
                        String json = OBJECT_MAPPER.writeValueAsString(qDto.getOptions());
                        q.setOptionsJson(json);
                    } catch (Exception e) {
                        q.setOptionsJson(null);
                    }
                } else {
                    q.setOptionsJson(null);
                }

                survey.addQuestion(q);
                i++;
            }
        }
    }
}