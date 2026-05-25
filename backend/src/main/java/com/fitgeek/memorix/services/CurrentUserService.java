package com.fitgeek.memorix.services;

import com.fitgeek.memorix.dtos.UpdatePasswordDto;
import com.fitgeek.memorix.dtos.UpdateUserDto;
import com.fitgeek.memorix.dtos.UserDto;
import com.fitgeek.memorix.entities.User;

public interface CurrentUserService {

    User getCurrentUser();
    UserDto UpdateUser(UpdateUserDto userDto);
    void updatePassword(UpdatePasswordDto dto);
    void deleteAccount();
}