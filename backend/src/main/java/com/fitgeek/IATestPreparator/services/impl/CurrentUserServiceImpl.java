package com.fitgeek.IATestPreparator.services.impl;

import com.fitgeek.IATestPreparator.dtos.UpdateUserDto;
import com.fitgeek.IATestPreparator.dtos.UserDto;
import com.fitgeek.IATestPreparator.entities.User;
import com.fitgeek.IATestPreparator.excpetion.BusinessException;
import com.fitgeek.IATestPreparator.repositories.UserRepository;
import com.fitgeek.IATestPreparator.services.CurrentUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    @Override
    public User getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(
                    "User not authenticated",
                    HttpStatus.UNAUTHORIZED
            );
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        "User not found",
                        HttpStatus.NOT_FOUND
                ));
    }

    @Override
    public UserDto UpdateUser(UpdateUserDto dto) {
        User user = getCurrentUser();

        user.setUsername(dto.username());
        user.setEmail(dto.email());

        userRepository.save(user);

        return new UserDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getCreatedAt()
        );
    }
}