package com.mj.mysns.domain.user.service;

import com.mj.mysns.domain.user.model.dto.UserDto;
import com.mj.mysns.domain.user.model.entity.User;

public interface UserService {

    UserDto findUserByFullNameAndEmail(UserDto userDto);

    UserDto saveUser(UserDto userDto);
}
