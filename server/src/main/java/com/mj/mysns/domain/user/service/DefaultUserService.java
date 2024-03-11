package com.mj.mysns.domain.user.service;

import com.mj.mysns.domain.user.model.dto.UserDto;
import com.mj.mysns.domain.user.model.entity.User;
import com.mj.mysns.domain.user.repository.UserRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findUserByEmail(UserDto userDto) {
        return userRepository.findByEmail(userDto.getEmail());
    }

    @Override
    public void saveUser(UserDto userDto) {
        User newUser = User.create(userDto.getUsername(), userDto.getFirst(),
            userDto.getLast(),
            userDto.getEmail(), userDto.getOauth2(), userDto.getProvider());
        userRepository.save(newUser);
    }
}
