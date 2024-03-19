package com.mj.mysns.user;

import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import java.util.Optional;
import org.springframework.security.core.Authentication;

public interface UserService {

    Optional<User> findByEmail(UserDto userDto);

    Optional<UserDto> findByIssuerAndSubject(UserDto userDto);

    Optional<User> findByAuthentication(Authentication authentication);

    void saveUser(UserDto userDto);

    Optional<UserDto> findByUsername(String username);
}
