package com.mj.mysns.config;

import com.mj.mysns.user.UserService;
import com.mj.mysns.user.dto.UserDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;


// 참고
// https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities-oauth2userservice
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomizedOidcUserPersistenceService extends OidcUserService {

    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        String issuer = userRequest.getIdToken().getIssuer().toString();
        String subject = userRequest.getIdToken().getSubject();

        Optional<UserDto> found = userService.checkUserByIssuerAndSubject(UserDto.builder().issuer(issuer).subject(subject).build());
        if (found.isEmpty()) {
            saveUser(oidcUser);
            log.info("인증된 사용자를 데이터베이스에 저장했습니다! {}", oidcUser.getEmail());
        }

        return oidcUser;

//        OAuth2AccessToken accessToken = userRequest.getAccessToken();
//        Set<GrantedAuthority> mappedAuthorities = new HashSet<>();
//
//        // TODO
//        // 1) Fetch the authority information from the protected resource using accessToken
//        // 2) Map the authority information to one or more GrantedAuthority's and add it to mappedAuthorities
//
//        // 3) Create a copy of oidcUser but use the mappedAuthorities instead
//        oidcUser = new DefaultOidcUser(mappedAuthorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    private void saveUser(OidcUser oidcUser) {
        UserDto userDto = UserDto.builder()
            .first(oidcUser.getGivenName())
            .last(oidcUser.getFamilyName())
            .email(oidcUser.getEmail())
            .issuer(oidcUser.getIssuer().toString())
            .subject(oidcUser.getSubject())
            .build();

        userService.saveUser(userDto);
    }
}
