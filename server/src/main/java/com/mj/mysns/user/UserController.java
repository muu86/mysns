package com.mj.mysns.user;

import com.mj.mysns.user.dto.UserDto;
import com.mj.mysns.user.payload.CreateUserPayload;
import com.mj.mysns.user.payload.UserResult;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = "/user")
public class UserController {

    private final UserService userService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Authentication user(Authentication authentication) {
        log.info("{}", authentication);
        return authentication;
    }

//    @GetMapping
//    public ResponseEntity<UserResult> getUser(@RequestParam("username") String username) {
//        Optional<UserDto> byUsername = userService.findByUsername(username);
//        if (byUsername.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        UserDto user = byUsername.get();
//        return ResponseEntity.ok(UserResult.builder().username(user.username()).build());
//    }

    @GetMapping
    public ResponseEntity<UserResult> getUserByIssuerAndSubject(
        @RequestParam("issuer") String issuer,
        @RequestParam("subject") String subject) {

        Optional<UserDto> byUsername = userService.findByIssuerAndSubject(UserDto.builder()
            .issuer(issuer)
            .subject(subject)
            .build());

        if (byUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        UserDto user = byUsername.get();
        return ResponseEntity.ok(UserResult.builder().username(user.username()).build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> createUser(@RequestBody CreateUserPayload payload) {
        UserDto userDto = UserDto.builder()
            .subject(payload.sub())
            .issuer(payload.iss())
            .first(payload.first())
            .last(payload.last())
            .email(payload.email())
            .emailVerified(payload.emailVerified())
            .build();

        userService.saveUser(userDto);
        return ResponseEntity.ok().build();
    }
}