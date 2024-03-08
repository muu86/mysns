# Nextjs 앱의 로그인 페이지와 Spring Security OAuth2를 연동하는 과정

## 개요

저는 이 프로젝트에서 스프링 시큐리티에서 제공하는 기능들을 활용해보고 공부하는 게 주 목적이므로 스프링 백엔드 서버가 클라이언트 역할과 리소스 서버 역할을 같이 하도록 개발하려고 합니다.

그런데 더 자연스러운 구성은 nextjs가 `인증 클라이언트`가 되어서 인증 과정을 거쳐 인가 서버로부터 토큰을 발급받고 스프링 API 서버는 `리소스 서버`가 되어 토큰 검증 후에 리소스를 내주는 형태일 것 같습니다. nextjs도 OAuth2 관련 라이브러리들(Auth0, Nextjs 등)이 있기 때문에 구현하는게 어렵지 않을 것 같습니다.

저는 OAuth2 인증 과정을 모두 스프링 시큐리티로 처리하고 싶고, JWT도 자세히 공부할 겸 스프링 서버가 인가 서버로부터 인증을 받고 nextjs로 JWT을 발급한 뒤 앞으로의 API 요청 헤더에 토큰을 포함시켜 검증하는 방식으로 방식으로 개발하려고 합니다.

## 인증 과정에서 커스터마이징이 필요한 부분

1. nextjs 로그인 페이지에서 OAuth2 로그인을 시도한다.

---

2. 권한부여 서버에서 사용자를 인증한다.
3. 인증을 완료하면 인증 코드를 발급받고
4. 인증 코드를 접근 토큰과 교환하기 위하여 스프링 서버와 인가 서버가 통신한다.
5. 인증이 완료된다.

---

4. **데이터베이스에 존재하지 않는 사용자를 저장한다.**
5. **백엔드 서버에서 프론트 url로 브라우저를 리다이렉트한다.**
6. **프론트로 JWT를 발급하고 api 요청 시 인증을 생략한다.**

4 ~ 6번을 처리하기 위해서 Security 설정을 커스터마이징해야 하고 nextjs와 스프링부트를 제가 원하는 방식으로 구성하는 예제도 거의 없어서 구현한 코드를 정리해보려고 합니다.

## Spring OAuth2 설정

### 커스터마이징 관련해서 기억할만한 패턴

아래 스프링 공식문서를 살펴보던 중 기존 서비스의 행동을 그대로 위임하면서 추가적인 행동만 확장하고 싶을 때 lambda식을 리턴하고 기존 서비스는 지역변수에 가두는 것을 확인했습니다. 마치 자바스크립트의 클로저처럼 작성된 코드가 신기해서 기록해놓습니다. 자바스크립트의 클로저는 내부 데이터를 조작할 수도 있으므로 동작 방식은 다르지만 객체를 생성하는 패턴은 같습니다.  
저는 커스터마이징한 클래스에서 다른 Bean이 필요해 질 것 같아서(UserEntity를 Repository에 저장하기 위한 Service 컴포넌트를 Autowired 하는 등) 이 패턴은 사용하지 않았습니다. 디폴트 클래스를 상속하고 `super.xxx()`를 호출하여 기존 행위를 위임했습니다.

https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities
![alt text](<../images/스크린샷 2024-03-08 오전 2.55.43.png>)

## OAuth2 인증된 사용자를 데이터베이스 저장하기

인가 서버에서 인증한 사용자를 데이터베이스에 저장하려고 합니다. oauth2 인증 단계 중 어느 단계에 유저를 저장하는 게 좋을 지를 고민했습니다. 인증이 성공한 경우 작동하는 `AuthenticationSuccessHandler`에서 하는 것도 가능하지만 클래스의 원래 역할이 무엇인지를 생각했을 때 `OAuth2UserService` 에서 처리하는 게 좋다고 판단했습니다.

#### OAuth2UserService의 역할

- access token을 사용해서 인가 서버의 userInfo 엔드포인트를 통해 userInfo를 조회합니다.
- 조회한 정보에서 `scope`를 바탕으로 스프링 시큐리티의 `GrantedAuthority` Set을 생성합니다.
- OpenID Connect 방식일 때는 `OidcUserService`가 사용되고 ID Token이 JWT로 발급되기 때문에 토큰에서 바로 스코프를 조회할 수 있습니다.

OpenID Connect 방식이라도 일정 조건에 해당하면 스코프를 다시 확인하기 위해 userInfo 엔드포인트에 request할 수도 있습니다.
OidcUserService 클래스의 소스코드 중
![alt text](<../images/스크린샷 2024-03-08 오전 3.29.18.png>)

#### AuthenticationSuccessHandler의 역할

인증이 성공한 사용자를 원래 요청 주소로 리다이렉트 시키는 것입니다.

### CustomOidcUserPersistenceService 클래스 작성

```java
// 참고
// https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities-oauth2userservice
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomOidcUserPersistenceService extends OidcUserService {

    // user 도메인 내의 서비스 빈
    private final UserService userService;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        if (shouldSaveUser(oidcUser)) {
            UserDto saved = saveUser(oidcUser);
            log.info("인증된 사용자를 데이터베이스에 저장했습니다! {}", saved.getEmail());
        }

        // ROLE을 user table에 저장하는 기능도 추가해야 할 듯

        return oidcUser;
    }

    private boolean shouldSaveUser(OidcUser oidcUser) {
        UserDto found = userService
            .findUserByFullNameAndEmail(UserDto.builder()
                .first(oidcUser.getGivenName())
                .last(oidcUser.getFamilyName())
                .email(oidcUser.getEmail())
                    .build());
        return found == null;
    }

    private UserDto saveUser(OidcUser oidcUser) {
        UserDto userDto = UserDto.builder()
            .username(oidcUser.getClaim("name"))
            .first(oidcUser.getGivenName())
            .last(oidcUser.getFamilyName())
            .email(oidcUser.getEmail())
            .oauth2(true)
            .provider("keycloak")
            .build();

        return userService.saveUser(userDto);
    }
}
```

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class OAuth2LoginSecurityConfig {

    private final OidcUserService customOidcUserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()));
        http
            .authorizeHttpRequests(requests -> requests.anyRequest().authenticated());
//        http
//            .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .oidcUserService(customOidcUserService))
                .successHandler(this.customOAuth2AuthenticationSuccessHandler())
            );

        return http.build();
    }

    @Bean
    AuthenticationSuccessHandler customOAuth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler();
    }

}
```

OAuth2 인증 후 데이터베이스에 유저 저장 확인
<image src="../images/스크린샷 2024-03-08 오전 4.32.51.png" width=1200>

## 인증이 완료되면 사용자를 프론트 페이지로 리다이렉트 시키기

### AuthenticationHandler

**AuthenticationSuccessHandler**은 OAuth2 인증이 성공하면 작동합니다. **onAuthenticationSuccess** 메서드가 **request**, **response**, **authentication** 객체를 받아 처리합니다. 주 용도는 로그인이 완료된 사용자를 원하는 location으로 redirect 시키는 것입니다.

구현체인 **SavedRequestAwareAuthenticationSuccessHandler** 사용자가 인증에 성공한 후에 원래 요청으로 리다이렉트됩니다. 주로 웹 애플리케이션에서 사용되며, 사용자가 보호된 페이지에 접근하려고 시도했지만 인증되지 않은 경우에 사용됩니다.

저는 인증이 완료된 사용자를 프론트 서버로 이동시키고 싶으므로 이 핸들러를 커스터마이징해서 `http://localhost:8080` 이 아니라 `http://localhost:3000`으로 보내려고 합니다.

```java
@Component
public class CustomOAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws ServletException, IOException {

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000");

        // 토큰 쿠키는 인증 토큰을 클라이언트로 보내고
        // 다음 api 요청에도 토큰을 첨부하도록 하기 위해서 넣었습니다.
        // 앞으로 스프링 서버를 oauth2-resource-server로도 활용하기 위해서입니다.
        // 일단 동작 테스트만 해봤고,
        // 브라우저에 쿠키가 저장되는 것만 확인했습니다.
        Cookie token = new Cookie("TOKEN", "1234");
        token.setHttpOnly(true);
        token.setSecure(false);
        token.setPath("/");
        response.addCookie(token);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

defaultTargetUrl을 true로 설정하면 defaultTargetUrl로 리다이렉트됩니다. SavedRequest의 url은 사용되지 않습니다.
그리고 **defaultTargetUrl**을 nextjs 앱의 주소로 설정했습니다.

부모 클래스 **SavedRequestAwareAuthenticationSuccessHandler** 문서
![alt text](<../images/스크린샷 2024-03-07 오후 11.55.06.png>)

또 한가지 포인트는 **RequestCache**를 이용해서 이전 request를 저장해 놓는 것입니다.
내부 처리 과정은 이런 것 같습니다.(아직 자세히 뒤져보지는 않았습니다...)

- 사용자가 보안 엔드포인트로 요청을 날리는데 인증된 사용자가 아니라 ExceptionalTranslationFilter가 작동한다.
- 이 필터가 인증처리를 시작
- login 페이지로 이동하거나 OAuth2 처리를 위해 브라우저를 navigate 시키게 된다.
- 인증이 완료되면 사용자가 원래 요청했던 엔드포인트로 이동시키기 위해 원래 요청을 저장해 놓을 필요가 있다.

저는 이걸 활용해서 프론트에서 사용자가 원래 요청했던 페이지로 리다이렉트시키려고 했지만 일단은 실패했습니다.
프론트의 html은 OAuth2 인증을 시작하기 위해서 스프링의 기본 엔드포인트인 `/oauth2/authoriztion/{registrationid}` 로 바로 이동합니다.  
아마도 인증이 실패해서 **ExceptionalTranslationFilter**를 거쳐서 oauth2가 시작된게 아니라 바로 oauth2 인증을 시작했기 때문에 request를 저장하지 않은 것으로 보입니다.
![alt text](<../images/스크린샷 2024-03-08 오전 12.08.59.png>)
당연히 `<a>`태그가 아니라 브라우저에 주소를 바로 입력했을 때도 request를 save하지 않습니다.
![alt text](<../images/스크린샷 2024-03-08 오전 12.20.04.png>)

일단은 인증 기능을 완성시켜 놓고 좀 더 나은 사용자 경험을 위한 사항은 개선해나가는 게 좋을 것 같습니다.

어쨋든 nextjs 주소로 리다이렉트되고 설정한 쿠키까지 브라우저로 전달되어 저장까지 된 것을 확인했습니다.
![alt text](<../images/스크린샷 2024-03-08 오전 12.25.10.png>)

## 인증된 사용자에게 jwt 토큰을 발급하고 앞으로의 api 요청을 검증하기

인증 서버가 보통 `access token`을 JWT로 발급하기 때문에 이 토큰을 그대로 사용자에게 전달하고 앞으로의 api 요청 헤더에 이를 포함시키면 될 것 같습니다. 토큰을 검증하는 건 spring `oauth2-resource-server` 라이브러리를 사용하면 간단할 것 같습니다.  
그 전에 공부 겸 테스트 용으로 직접 JWT를 인코딩해서 사용자에게 발급하고 디코딩하는 코드를 작성해보려고 합니다.
