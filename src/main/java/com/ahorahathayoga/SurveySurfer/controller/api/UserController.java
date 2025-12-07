package com.ahorahathayoga.SurveySurfer.controller.api;


import com.ahorahathayoga.SurveySurfer.dto.ApiErrorResponse;
import com.ahorahathayoga.SurveySurfer.dto.user.UserViewDto;
import com.ahorahathayoga.SurveySurfer.enums.UserRole;
import com.ahorahathayoga.SurveySurfer.model.User;
import com.ahorahathayoga.SurveySurfer.repository.UserRepository;
import com.ahorahathayoga.SurveySurfer.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping()
    public ResponseEntity<?> users(@RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20") int size, HttpServletRequest http
    )
    {
        if (page < 0 || page > 1000)
            return badRequest("Page index must be >= 0 and <= 1000.", http.getRequestURI());


        if (size <= 0 || size > 100)
            return badRequest("Page size must be between 1 and 100.", http.getRequestURI());

        Pageable pageable = PageRequest.of(page, size);
        Page<UserViewDto> usersPage = userService.findAll(pageable);
        return ResponseEntity.ok(usersPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> findUser(@PathVariable Long id, HttpServletRequest httpReq)
    {
        Optional<UserViewDto> user = userService.findById(id);
        if(user.isEmpty()){
            return notFound("User not found", httpReq.getRequestURI());
        }
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/role")
    public ResponseEntity<?> changeUserRole(@PathVariable Long id, @RequestBody UserRole role, HttpServletRequest httpReq)
    {
        //Falta cambiar la validación. Ahora mismo si se envia un rol diferente de lo que hay en enum, devuelve 403
        if(role != UserRole.ADMIN && role != UserRole.RESEARCHER && role != UserRole.PUBLIC){ //nunca entra aqui (crear un dto de validacion)
            return badRequest("Role doesn't exist", httpReq.getRequestURI());
        }
        User user = userService.findOne(id);
        if(user == null){
            return notFound("User not found", httpReq.getRequestURI());
        }
        user.setRole(role);
        UserViewDto userViewDto = new UserViewDto(
        );
        userViewDto.setId(user.getId());
        userViewDto.setUsername(user.getUsername());
        userViewDto.setEmail(user.getEmail());
        userViewDto.setRole(user.getRole());
        return ResponseEntity.ok(userViewDto);
    }




    //responses

    private ResponseEntity<ApiErrorResponse> notFound(String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    private ResponseEntity<ApiErrorResponse> badRequest(String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.badRequest().body(error);
    }

    private ResponseEntity<ApiErrorResponse> unauthorized(String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.UNAUTHORIZED.value())
                .error(HttpStatus.UNAUTHORIZED.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }



    private ResponseEntity<ApiErrorResponse> noContent(String message, String path) {
        ApiErrorResponse error = ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(HttpStatus.NO_CONTENT.value())
                .error(HttpStatus.NO_CONTENT.getReasonPhrase())
                .message(message)
                .path(path)
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(error);
    }
}
