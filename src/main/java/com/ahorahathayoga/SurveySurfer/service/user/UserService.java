package com.ahorahathayoga.SurveySurfer.service.user;

import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.Optional;


public interface UserService {

    Page<UserViewDto> findAll(Pageable pageable);

    Optional<UserViewDto> findById(Long id);

    User findOne(Long id);

    User update(User user);
}
