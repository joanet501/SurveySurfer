package com.ahorahathayoga.SurveySurfer.dto.user;

import com.ahorahathayoga.SurveySurfer.enums.QuestionType;
import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import lombok.Data;

@Data
public class UserViewDto {
    private Long id;
    private String username;
    private String email;
    private UserRole role;
}
