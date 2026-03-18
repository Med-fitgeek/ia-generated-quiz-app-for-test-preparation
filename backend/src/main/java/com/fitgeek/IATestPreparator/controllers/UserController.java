package com.fitgeek.IATestPreparator.controllers;

import com.fitgeek.IATestPreparator.dtos.UpdateUserDto;
import com.fitgeek.IATestPreparator.dtos.UserDto;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.services.CurrentUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final CurrentUserService currentUserService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {

        User user = currentUserService.getCurrentUser();

        return ResponseEntity.ok(
                new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
                )
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> updateUser(
            @RequestBody UpdateUserDto dto
    ) {

        UserDto updatedUser = currentUserService.UpdateUser(dto);
        return ResponseEntity.ok(updatedUser);
    }

}
