package com.fitgeek.IATestPreparator.services;

import com.fitgeek.IATestPreparator.dtos.UpdateUserDto;
import com.fitgeek.IATestPreparator.dtos.UserDto;
import com.fitgeek.IATestPreparator.entities.User;

public interface CurrentUserService {

    User getCurrentUser();
    UserDto UpdateUser(UpdateUserDto userDto);
}