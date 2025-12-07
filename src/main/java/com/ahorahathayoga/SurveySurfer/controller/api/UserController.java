package com.ahorahathayoga.SurveySurfer.controller.api;


import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> users(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size
    )
    {
        if (page < 0 || page > 1000) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Page index must be >= 0 and <= 1000.")
            );
        }

        if (size <= 0 || size > 100) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Page size must be between 1 and 100")
            );
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<UserViewDto> usersPage = userService.findAll(pageable);
        return ResponseEntity.ok(usersPage);
    }


}
