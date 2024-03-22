package com.mj.mysns.user.repository;

import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import java.util.Optional;

public interface CustomizedUserRepository {

    Optional<User> findUserProfile(UserDto userDto);

}
