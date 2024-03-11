package com.mj.mysns.domain.user.service;

import com.mj.mysns.domain.user.model.dto.UserDto;
import com.mj.mysns.domain.user.model.entity.User;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserByEmail(UserDto userDto);

    void saveUser(UserDto userDto);
}
