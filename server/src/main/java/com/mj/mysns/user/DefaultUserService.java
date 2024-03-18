package com.mj.mysns.user;

import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public Optional<User> findUserByAuthentication(Authentication authentication) {

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 구현체가 무엇인지 확인할 필요가 있다.
        // 일단 oidc 로만 개발했기 때문에
        // 이것만 체크
        // 토큰으로 어떻게 user 를 특정할 것인지도 생각해봐야
        Jwt token = (Jwt) authentication.getPrincipal();;
        Optional<User> found = userRepository.findByEmail(token.getClaimAsString("email"));
        return found;
    }

    @Override
    public void saveUser(UserDto userDto) {
        User created = User.builder()
            .username(userDto.getUsername())
            .first(userDto.getFirst())
            .last(userDto.getLast())
            .email(userDto.getEmail())
            .oauth2(userDto.getOauth2())
            .provider(userDto.getProvider())
            .build();
        userRepository.save(created);
    }
}
