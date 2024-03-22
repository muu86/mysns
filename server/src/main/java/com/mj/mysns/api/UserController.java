package com.mj.mysns.api;

import com.mj.mysns.api.payload.CreateUserPayload;
import com.mj.mysns.api.result.UserProfileResult;
import com.mj.mysns.user.UserService;
import com.mj.mysns.user.dto.UserDto;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Slf4j

@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Authentication user(Authentication authentication) {
        log.info("{}", authentication);
        return authentication;
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResult> getUserProfile(@RequestParam(value = "username") String username) {

        if (username.isEmpty()) return ResponseEntity.badRequest().build();

        Optional<UserDto> found = userService.getUserProfileByUsername(
            UserDto.builder().username(username).build());

        return found.map(userDto -> UserProfileResult.builder()
                .username(userDto.username())
                .babyAge(userDto.babyAge())
                .content(userDto.content())
                .userFiles(userDto.userFiles())
                .userAddresses(userDto.userAddresses()).build())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/exists")
    public ResponseEntity<Map<String, String>> checkUserExistsByIssuerAndSubject(
        @RequestParam("issuer") String issuer,
        @RequestParam("subject") String subject) {

        if (issuer.isEmpty() || subject.isEmpty()) return ResponseEntity.badRequest().build();

        Optional<UserDto> byUsername = userService.checkUserByIssuerAndSubject(UserDto.builder()
            .issuer(issuer)
            .subject(subject)
            .build());

        return byUsername.map(userDto -> Map.of("message", "success", "username", byUsername.get().username()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createUser(@Valid CreateUserPayload payload) {
        UserDto userDto = UserDto.builder()
            .subject(payload.sub())
            .issuer(payload.iss())
            .first(payload.first())
            .last(payload.last())
            .email(payload.email())
            .emailVerified(payload.emailVerified())
            .username(payload.username())
            .babyAge(payload.babyAge())
            .legalAddressCode(payload.legalAddressCode())
            .build();

        userService.saveUser(userDto);

        return ResponseEntity.ok(Map.of("message", "success"));
    }
}