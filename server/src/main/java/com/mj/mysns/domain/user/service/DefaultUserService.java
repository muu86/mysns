package com.mj.mysns.domain.user.service;

import com.mj.mysns.domain.user.model.dto.UserDto;
import com.mj.mysns.domain.user.model.entity.User;
import com.mj.mysns.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto saveUser(UserDto userDto) {
        User newUser = User.create(userDto.getUserName(), userDto.getFirstName(),
            userDto.getLastName(),
            userDto.getEmail(), userDto.getOauth2(), userDto.getProvider());
        User saved = userRepository.save(newUser);
        // 일단 매개변수 그대로 리턴
        return userDto;
    }
}
