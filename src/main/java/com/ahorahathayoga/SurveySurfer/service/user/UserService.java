package com.ahorahathayoga.SurveySurfer.service.user;

import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UserService {

    Page<UserViewDto> findAll(Pageable pageable);

}
