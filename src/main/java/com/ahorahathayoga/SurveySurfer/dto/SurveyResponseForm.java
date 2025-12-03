package com.ahorahathayoga.SurveySurfer.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class SurveyResponseForm {

    /**
     * Map from questionId → submitted value.
     * HTML field name will be like answers[questionId].
     */
    private Map<Long, String> answers;
}