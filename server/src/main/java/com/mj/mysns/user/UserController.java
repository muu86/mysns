package com.mj.mysns.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    public Authentication user(Authentication authentication) {
        log.info("{}", authentication);
        return authentication;
    }
}