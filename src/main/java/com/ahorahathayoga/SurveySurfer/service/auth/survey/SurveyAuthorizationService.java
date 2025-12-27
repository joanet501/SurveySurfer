package com.ahorahathayoga.SurveySurfer.service.survey;

import com.ahorahathayoga.SurveySurfer.enums.SurveyStatus;
import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import com.ahorahathayoga.SurveySurfer.model.Survey;
import com.ahorahathayoga.SurveySurfer.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class SurveyAuthorizationService {

    public void checkCanEdit(Survey survey, User currentUser) {
        // Only owner and ADMIN can edit
        if (!survey.getUser().getId().equals(currentUser.getId()) && currentUser.getRole()== UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this survey");
        }

        // If published, cannot edit structure
        if (survey.getStatus() == SurveyStatus.PUBLISHED) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cannot edit a published survey");
        }
    }

    public void checkCanDelete(Survey survey, User currentUser) {
        // Only owner and ADMIN can delete
        if (!survey.getUser().getId().equals(currentUser.getId())  && currentUser.getRole()== UserRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the owner of this survey");
        }

        // Can only delete drafts
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Can only delete draft surveys");
        }
    }

    public void checkCanView(Survey survey, User currentUser) {
        // If draft, only owner and ADMIN can view
        if (survey.getStatus() == SurveyStatus.DRAFT) {
            if (currentUser == null || !survey.getUser().getId().equals(currentUser.getId()) && currentUser.getRole()== UserRole.ADMIN) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This survey is not published");
            }
        }
        // If published, anyone can view (adjust based on your needs)
    }

    public void checkCanViewSubmissions(Survey survey, User currentUser) {
        boolean isOwner = survey.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to view submissions for this survey");
        }
    }
}