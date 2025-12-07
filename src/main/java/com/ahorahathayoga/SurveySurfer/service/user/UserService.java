package com.ahorahathayoga.SurveySurfer.service.user;

import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    Page<UserViewDto> findAll(Pageable pageable);

    UserViewDto findById(Long id);

    User update(User user);
}
