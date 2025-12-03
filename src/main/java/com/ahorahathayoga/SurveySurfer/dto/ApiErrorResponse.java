package com.ahorahathayoga.SurveySurfer.dto;

import lombok.*;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    /**
     * Extra details, like field errors.
     */
    private Map<String, Object> details;
}