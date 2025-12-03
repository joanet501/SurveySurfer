package com.ahorahathayoga.SurveySurfer.util;

import com.ahorahathayoga.SurveySurfer.dto.SurveyViewDto;
import com.ahorahathayoga.SurveySurfer.model.Question;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyMapper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static SurveyViewDto toSurveyViewDto(Survey survey) {
        return SurveyViewDto.builder()
                .id(survey.getId())
                .title(survey.getTitle())
                .description(survey.getDescription())
                .slug(survey.getSlug())
                .questions(
                        survey.getQuestions().stream()
                                .sorted((q1, q2) -> {
                                    int cmp = Integer.compare(
                                            q1.getDisplayOrder() == null ? 0 : q1.getDisplayOrder(),
                                            q2.getDisplayOrder() == null ? 0 : q2.getDisplayOrder()
                                    );
                                    if (cmp == 0) {
                                        return Long.compare(
                                                q1.getId() == null ? 0L : q1.getId(),
                                                q2.getId() == null ? 0L : q2.getId()
                                        );
                                    }
                                    return cmp;
                                })
                                .map(SurveyMapper::toQuestionViewDto)
                                .collect(Collectors.toList())
                )
                .build();
    }

    private static SurveyViewDto.QuestionViewDto toQuestionViewDto(Question question) {
        return SurveyViewDto.QuestionViewDto.builder()
                .id(question.getId())
                .text(question.getText())
                .type(question.getType())
                .required(question.isRequired())
                .displayOrder(question.getDisplayOrder())
                .options(parseOptions(question.getOptionsJson()))
                .build();
    }

    private static List<String> parseOptions(String optionsJson) {
        if (optionsJson == null || optionsJson.isBlank()) {
            return Collections.emptyList();
        }
        try {
            // Expecting a simple JSON array of strings like ["Sí","No","Tal vez"]
            return OBJECT_MAPPER.readValue(optionsJson, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            // In case it's not a simple list, just ignore
            return Collections.emptyList();
        }
    }
}