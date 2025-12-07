package com.ahorahathayoga.SurveySurfer.service.user;

import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    @Override
    public Page<UserViewDto> findAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(this::convertToDto);
    }

    @Override
    public UserViewDto findById(Long id) {
        return convertToDto(userRepository.findById(id).orElse(null));
    }

    @Override
    public User update(User user) {
        return null;
    }

    // helpers
    private UserViewDto convertToDto(User user) {
        return modelMapper.map(user, UserViewDto.class);
    }
}
