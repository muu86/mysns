package com.mj.mysns.user;

import com.mj.mysns.common.exception.AddressNotFoundException;
import com.mj.mysns.common.exception.DuplicatedUserException;
import com.mj.mysns.common.exception.DuplicatedUsernameException;
import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.entity.User;
import com.mj.mysns.user.entity.UserLegalAddress;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DefaultUserService implements UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Override
    public Optional<User> findByEmail(UserDto userDto) {
        return userRepository.findByEmail(userDto.email());
    }

    @Override
    public Optional<UserDto> findByIssuerAndSubject(UserDto userDto) {
        Optional<User> found = userRepository.findByIssuerAndSubject(userDto.issuer(),
            userDto.subject());

        if (found.isEmpty()) {
            return Optional.empty();
        }

        User user = found.get();
        return Optional.of(UserDto.builder()
            .username(user.getUsername())
            .email(user.getEmail())
            .first(user.getFirst())
            .last(user.getLast())
            .issuer(user.getIssuer())
            .subject(user.getSubject())
            .build());
    }

    @Override
    public Optional<User> findByAuthentication(Authentication authentication) {
        Jwt token = (Jwt) authentication.getPrincipal();;
        Optional<User> found = userRepository.findByEmail(token.getClaimAsString("email"));
        return found;
    }

    @Override
    public void saveUser(UserDto userDto) {
        Optional<User> byIssuerAndSubject = userRepository.findByIssuerAndSubject(userDto.issuer(),
            userDto.subject());
        if (byIssuerAndSubject.isPresent()) {
            throw new DuplicatedUserException("user 가 이미 존재합니다");
        }

        Optional<User> byUsername = userRepository.findByUsername(userDto.username());
        if (byUsername.isPresent()) {
            throw new DuplicatedUsernameException();
        }

        Optional<LegalAddress> byCode = addressRepository.findByCode(userDto.legalAddressCode());
        if (byCode.isEmpty()) {
            throw new AddressNotFoundException("주소를 찾을 수 없습니다.");
        }

        User toSave = User.builder()
            .username(userDto.username())
            .first(userDto.first())
            .last(userDto.last())
            .email(userDto.email())
            .emailVerified(userDto.emailVerified())
            .issuer(userDto.issuer())
            .subject(userDto.subject())
            .babyAge(userDto.babyAge())
            .build();
        toSave.getUserAddress().add(new UserLegalAddress(toSave, byCode.get()));

        User save = userRepository.save(toSave);
        log.info("user: {} 저장, id: {}", save.getEmail(), save.getUserId());
    }

    @Override
    public Optional<UserDto> findByUsername(String username) {
        Optional<User> byUsername = userRepository.findByUsername(username);
        if (byUsername.isEmpty()) {
            return Optional.empty();
        }

        User found = byUsername.get();
        return Optional.of(UserDto.builder()
            .username(found.getUsername())
            .first(found.getFirst())
            .last(found.getLast())
            .email(found.getEmail())
            .build());
    }
}
