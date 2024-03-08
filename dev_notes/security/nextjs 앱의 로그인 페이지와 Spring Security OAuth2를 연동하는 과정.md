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
6. **프론트로 JWT를 발급해서 api 요청 시 토큰으로 인증한다**

4 ~ 6번을 처리하기 위해서 Security 설정을 커스터마이징해야 하고 nextjs와 스프링부트를 제가 원하는 방식으로 구성하는 예제도 거의 없어서 구현한 코드를 정리해보려고 합니다.

## Spring OAuth2 설정

## OAuth2 인증된 사용자를 데이터베이스 저장하기

인가 서버에서 인증한 사용자를 데이터베이스에 저장하려고 합니다. oauth2 인증 단계 중 어느 단계에 유저를 저장하는 게 좋을 지를 고민했습니다. 인증이 성공한 경우 작동하는 `AuthenticationSuccessHandler`에서 하는 것도 가능하지만 클래스의 원래 역할이 무엇인지를 생각했을 때 `OAuth2UserService` 에서 처리하는 게 좋다고 판단했습니다.

#### OAuth2UserService의 역할

- access token을 사용해서 인가 서버의 userInfo 엔드포인트를 통해 userInfo를 조회합니다.
- 조회한 정보에서 `scope`를 바탕으로 스프링 시큐리티의 `GrantedAuthority` Set을 생성합니다.
- OpenID Connect 방식일 때는 `OidcUserService`가 사용되고 ID Token이 JWT로 발급되기 때문에 토큰에서 바로 스코프를 조회할 수 있습니다.

OpenID Connect 방식이라도 일정 조건에 해당하면 스코프를 다시 확인하기 위해 userInfo 엔드포인트에 request할 수도 있습니다.

`OidcUserService 클래스의 소스코드 중`
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

**alwaysUseDefaultTargetUrl**을 true로 설정하면 항상 defaultTargetUrl로 리다이렉트됩니다. SavedRequest은 사용되지 않습니다.
**defaultTargetUrl**을 프론트 주소로 설정했습니다.

부모 클래스 **SavedRequestAwareAuthenticationSuccessHandler**
![alt text](<../images/스크린샷 2024-03-07 오후 11.55.06.png>)

**RequestCache**에 저장해 놓은 이전 request를 조회하는 것을 확인할 수 있습니다.
내부 처리 과정은 이런 것 같습니다.(아직 자세히 뒤져보지는 않았습니다...)

- 사용자가 보안 엔드포인트로 요청을 날리는데 인증된 사용자가 아니라 ExceptionalTranslationFilter가 작동한다.
- 이 필터가 인증처리를 시작
- login 페이지로 이동하거나 OAuth2 처리를 위해 브라우저를 navigate 시키게 된다.
- 인증이 완료되면 사용자가 원래 요청했던 엔드포인트로 이동시키기 위해 원래 요청을 저장해 놓을 필요가 있다.

저는 이걸 활용해서 프론트에서 사용자가 원래 요청했던 페이지로 리다이렉트시키려고 했지만 일단은 실패했습니다.
프론트의 html은 OAuth2 인증을 시작하기 위해서 스프링의 기본 엔드포인트인 `/oauth2/authoriztion/{registrationid}` 로 바로 이동합니다.  
아마도 인증이 실패하고 **ExceptionalTranslationFilter**를 거쳐서 `/oauth2/authoriztion/{registrationid}`로 온게 아니라 바로 oauth2 인증을 시작했기 때문에 request를 저장하지 않은 것으로 보입니다. request를 저장하는 로직은 ExceptionalTranslationFilter에 있을 것 같네요.

![alt text](<../images/스크린샷 2024-03-08 오전 12.08.59.png>)
당연히 `<a>`태그가 아니라 브라우저에 주소를 바로 입력했을 때도 request를 save하지 않습니다.
![alt text](<../images/스크린샷 2024-03-08 오전 12.20.04.png>)

일단은 인증 기능을 완성시켜 놓고 좀 더 나은 사용자 경험을 위한 사항은 개선해나가는 게 좋을 것 같습니다.

어쨋든 nextjs 주소로 리다이렉트되고 설정한 쿠키까지 브라우저로 전달되어 저장까지 된 것을 확인했습니다.
![alt text](<../images/스크린샷 2024-03-08 오전 12.25.10.png>)

## 인증된 사용자에게 jwt 토큰을 발급하고 앞으로의 api 요청을 검증하기

인증 서버가 보통 `access token`을 JWT로 발급하기 때문에 이 토큰을 그대로 사용자에게 전달하고 앞으로의 api 요청 헤더에 이를 포함시키면 될 것 같습니다. 토큰을 검증하는 건 spring `oauth2-resource-server` 라이브러리를 사용하면 간단할 것 같습니다.  
그 전에 공부 겸 테스트 용으로 직접 JWT를 직접 사용자에게 발급하는 코드를 작성해보려고 합니다.

### 암호화 이해

#### 무결성 검사(MessageDigest)

- 원본이 전송 과정에서 변경되지 않았음을 확인하는 것.
- 입력 값으로 전달된 데이터를 고정 길이의 해쉬 값으로 출력.
- 해쉬 함수로 도출된 해쉬값은 원본 값을 도출하는 것이 거의 불가능하다.(https://docs.spring.io/spring-security/reference/features/authentication/password-storage.html 레인보우 테이블과 Salt의 삽입)
- A는 B에게 원본, 해쉬값, 알고리즘을 보낸다.
- B는 원본에 알고리즘을 적용해서 해쉬값 도출. A에게서 받은 해쉬와 도출해낸 해쉬를 비교.
- 원본이 네트워크 상에서 변경되지 않았음을 검증한다.

#### 서명

- 비대칭키 사용
- 개인키로 암호화하고 공개키로만 복호화할 수 있다.
- 서명이란 원본을 개인키로 암호화한 해쉬값
- A는 B에게 원본과 서명, 공개키를 보낸다.
- B는 서명을 공개키로 복호화하고 그 값을 원본과 비교.
- 공개키를 A가 제공했기 때문에 원본을 A가 작성한 것으로 신뢰할 수 있음.

```java
    @Test
    void signature() throws Exception {
        // 비대칭키 생성
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();

        // 개인키로 서명
        byte[] digest = message.getBytes("UTF-8");
        Signature sig = Signature.getInstance("SHA256WithRSA");
        sig.initSign(keyPair.getPrivate());
        sig.update(digest);
        byte[] signed = sig.sign();

        // 공개키로 검증
        sig.initVerify(keyPair.getPublic());
        sig.update(digest);

        boolean verified = sig.verify(signed);

        assertEquals(verified, true);
    }
```

![alt text](<../images/스크린샷 2024-03-08 오후 4.38.59.png>)

```java
@Test
void rsa() throws Exception {
String algorithm = "RSA";

    // 키페어 생성
    KeyPairGenerator gen = KeyPairGenerator.getInstance(algorithm);
    gen.initialize(1024, new SecureRandom());
    KeyPair keyPair = gen.generateKeyPair();

    // 공개키로 encrypt
    Cipher cipher = Cipher.getInstance(algorithm);
    cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
    byte[] encrypted = cipher.doFinal(message.getBytes());

    // encode
    byte[] encoded = Base64.getEncoder().encode(encrypted);

    // 검증 시작

    Cipher cipher2 = Cipher.getInstance(algorithm);
    // decode
    byte[] decoded = Base64.getDecoder().decode(encoded);
    // decode 결과 확인
    assertArrayEquals(encrypted, decoded);

    // 개인키로 decrypt
    cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
    byte[] decrypted = cipher.doFinal(decoded);
    String decryptedMessage = new String(decrypted);
    // decrypt 결과 확인
    assertArrayEquals(message.getBytes(), decrypted);

}

```

### JWT

구조

- Header
  - 토큰유형(JWT), 서명 알고리즘 지정
- Paylaod
  - 토큰에 포함할 내용(claims)
- Signature
  - 헤더와 페이로드를 인코딩한 후 서명한다.

```js
HMAC_SHA256(
  secret,
  base64urlEncoding(header) + '.' + base64urlEncoding(payload)
);
```

JWT = Base64(header).Base64(payload).Base64(signature)!!

### JWK

암호화 키를 저장하는 방식으로 인가서버에서 발행하는 JWT 토큰의 암호화 및 서명에 필요한 암호화 키의 다양한 정보를 담은 JSON 객체 표준입니다. `jwtSetUri` 정보를 설정하면 인가 서버로부터 JWK 형태의 정보를 다운로드할 수 있습니다.
OAuth2에서 인가서버는 `/.well-known/openid-configuration`에서 `jwks_uri`를 공개한다.

```java
    @Test
    void jwk() throws JOSEException {
        // 비대칭키
        RSAKey rsaKey = new RSAKeyGenerator(2048)
            .keyID("rsa-kid")
            .keyUse(KeyUse.SIGNATURE)
            .keyOperations(Set.of(KeyOperation.SIGN))
            .algorithm(JWSAlgorithm.RS512)
            .generate();

        // 대칭키
        OctetSequenceKey octetSecretKey = new OctetSequenceKeyGenerator(256)
            .keyID("secret-kid")
            .keyUse(KeyUse.SIGNATURE)
            .keyOperations(Set.of(KeyOperation.SIGN)  )
            .algorithm(JWSAlgorithm.HS384)
            .generate();

        // 2개의 키를 가지고 JWK Set 생성
        JWKSet jwkSet = new JWKSet(List.of(rsaKey, octetSecretKey));

        // key set에서 원하는 키 선택
        JWKSource<SecurityContext> jwkSource = ((jwkSelector, context) -> jwkSelector.select(jwkSet));
        JWKSelector rsaSelector = new JWKSelector(new Builder().keyID("rsa-kid").build());
        List<JWK> jwks = jwkSource.get(rsaSelector, null);
        JWK jwk = jwks.getFirst();

        assertEquals("rsa-kid", jwk.getKeyID());
    }
```

### spring security oauth2 resource server 라이브러리를 이용하여 jwt 검증하기

기본 `SavedRequestAwareAuthenticationSuccessHandler`를 커스터마이징 했던 `CustomOAuth2AuthenticationSuccessHandler` 클래스에서 인가 서버로부터 발급받은 `idtoken` 을 그대로 클라이언트로 넘겨주도록 했습니다.

```java
public class CustomOAuth2AuthenticationSuccessHandler extends
    SavedRequestAwareAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws ServletException, IOException {

        this.setAlwaysUseDefaultTargetUrl(true);
        this.setDefaultTargetUrl("http://localhost:3000");
        Cookie token = new Cookie("TOKEN", "1234");
//        token.setHttpOnly(true);
        token.setSecure(false);
        token.setPath("/");
//        response.addCookie(token);

        // response header 추가
        // 인증을 위해 Bearer 토큰을 요청 시 첨부하라고 클라이언트에게 알려줌
        response.addHeader("Authentication", "Bearer " + ((DefaultOidcUser) authentication.getPrincipal()).getIdToken().getTokenValue());

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

Oidc 인증이므로 `Principal` 인터페이스는 `DefaultOidcUser`로 구체화되었습니다.
![alt text](<../images/스크린샷 2024-03-08 오후 10.40.09.png>)

리소스 서버가 인가 서버가 발급한 jwt를 검증하기 위해서는 공개키가 필요합니다. @Configuration 클래스에 JWTDecoder 빈을 추가합니다.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.
    // ...

    // 리소스 서버 사용하여 api 요청 시 jwt 토큰을 검증하도록 함
    http
        .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));

    return http.build();
}

@Bean
JwtDecoder jwtDecoder() {
    return JwtDecoders.fromIssuerLocation("http://localhost:3333/realms/master");
}
```

`jwks_uri`엔드포인트에서 제공하는 공개키 정보. n 과 e 속성이 공개키를 구성한다.
![alt text](<../images/스크린샷 2024-03-08 오후 10.51.52.png>)

OAuth2 사양에 따라서 인가 서버는 메타데이터를 `{issuer}/.well-known/{}` 엔드포인트에 공개하고 있습니다. 스프링 리소스 서버는 인가 서버를 가리키는 `issuer` 주소만 제공해주면 알아서 메타데이터를 뽑아오고 `jwks_uri` 속성을 조회하여 jwk set을 받아와 JwtDecoder 빈을 생성합니다.

#### 인증 흐름

- 사용자가 OAuth2 로그인 성공하면
- SuccessHandler에서 인가서버로부터 받은 jwt을 그대로 전달해줌 (Response의 Authentication 헤더를 통해)
- 매 요청마다 스프링 리소소 서버는 공개키로 토큰 검증

스프링 리소스 서버에서 검증은 `BearerTokenAuthenticationFilter`에서 일어난다.
https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html

```java
    @Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token;
		try {
			token = this.bearerTokenResolver.resolve(request);
		}
        // ...

        // 필터에서 Bearer Token을 검증해서 Security Context를 생성하는 것을 확인할 수 있다.
		BearerTokenAuthenticationToken authenticationRequest = new BearerTokenAuthenticationToken(token);
		authenticationRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

		try {
			AuthenticationManager authenticationManager = this.authenticationManagerResolver.resolve(request);
			Authentication authenticationResult = authenticationManager.authenticate(authenticationRequest);
			SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
			context.setAuthentication(authenticationResult);
			this.securityContextHolderStrategy.setContext(context);
			this.securityContextRepository.saveContext(context, request,
            // ...
			filterChain.doFilter(request, response);
		}
		catch (AuthenticationException failed) {
			this.securityContextHolderStrategy.clearContext();
			this.logger.trace("Failed to process authentication request", failed);
			this.authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
		}
	}
```

SuccessHandler에서 전달한 토큰이 Response Header 에 담겨 전달되고
![alt text](<../images/스크린샷 2024-03-08 오후 11.12.51.png>)

이를 postman을 이용해서 Request Header에 담아 전달했을 때 200 status 확인할 수 있습니다.
![alt text](<../images/스크린샷 2024-03-08 오후 11.11.12.png>)

## Nextjs 앱에서 토큰 다루기

### OAuth2 인증 시작하고 토큰 받기

`<a>` 태그로 oauth2 인증 엔드포인트로 사용자롤 보냅니다.

```ts
// app/login/page.tsx
export default function Page() {
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="w-72 rounded-md border-solid border-2 border-gray-400 flex-col items-center justify-center">
        <div className="py-3 text-center">
          <p className="font-bold">login</p>
        </div>
        <div className="py-3 text-center">
          <a href="http://localhost:8080/oauth2/authorization/keycloak">
            keycloak
          </a>
        </div>
      </div>
    </main>
  );
}
```

인증이 완료되면 스프링 시큐리티에서 설정한 대로 프론트 페이지로 리다이렉트되고 제가 첨부한 쿠키가 브라우저에 저장된 것을 확인할 수 있습니다.
![alt text](<../images/스크린샷 2024-03-09 오전 4.32.02.png>)

### 토큰을 첨부하여 API 요청하기

```ts
// app/lib/action.ts
'use server';

import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

const SERVER_URL = 'http://localhost:8080';

export async function getUser() {
  const cookieStore = cookies();

  if (!cookieStore.get('tkn')) {
    redirect('/login');
  }

  return await fetch(`${SERVER_URL}/user`, {
    headers: {
      Authorization: `Bearer ${cookieStore.get('tkn')?.value}`,
    },
  })
    .then((res) => {
      if (`${res.status}`.startsWith('4'))
        throw new Error('인증에 실패했습니다.');

      if (!res.ok) throw new Error(`${res.status} status! 확인필요!`);

      return res.json();
    })
    .catch((err) => ({ err: true, message: err.message }));
}
```

`/user` 핸들러는 간단히 Authentication 객체를 리턴합니다.

```java
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
```

nextjs에서 제공하는 `cookies` 함수로 쿠키 정보를 가져올 수 있습니다. 쿠키는 악용될 가능성이 크기 때문에 `HttpOnly` 옵션을 사용해서 자바스크립트에서는 접근이 불가능하도록 하는 것이 일반적입니다. 브라우저 콘솔에서 쿠키에 접근하려고 `document.cookies`를 조회해도 HttpOnly 옵션이 걸려있는 쿠키는 확인할 수가 없습니다.
![alt text](<../images/스크린샷 2024-03-09 오전 4.39.50.png>)

그런데 nextjs는 서버 사이드 렌더링을 위한 라이브러리로서 사용자와 상호작용해야 하는 `Client Component` 외에는 서버에서 자바스크립트 코드를 실행하기 때문에 쿠키를 조작해도 안전하다고 볼 수 있습니다. 주의할 것은 Server Component 에서는 쿠키를 조회할 수만 있고 수정하는 것은 불가능합니다.

Server Component를 렌더링하는 방식에는 `Streaming`이 있습니다. 서버에서 모든 렌더링을 마친 뒤에 브라우저로 보내주는게 아니라 chunk로 나뉘어진 컴포넌트들을 완성되는 대로 병렬적으로 보내주는 것입니다.
https://nextjs.org/docs/app/api-reference/functions/cookies#cookiessetname-value-options
Nextjs 공식 문서에서 Http가 스트리밍이 시작된 뒤에는 쿠키를 수정하지 못 하도록 막고 있기 때문에 쿠키를 수정하고 삭제하는 건 `Server Action`과 `Route Handler`만 가능하다고 명시하고 있습니다. Server Component 에서 import 한 Server Action 함수를 사용하면 간단히 쿠키를 가져오고 수정도 할 수 있겠네요.

```ts
// app/user/page.tsx
import { getUser } from '@/app/lib/actions';

export default async function Page() {
  const userData = await getUser();
  // console.log(userData);
  return (
    <main className="flex min-h-screen flex-col items-center justify-between p-24">
      <div className="w-72 px-3 rounded-md border-solid border-2 border-gray-400 flex-col items-center justify-center">
        <div className="py-3 text-center">
          <p className="font-bold">user</p>
        </div>
        <div className="py-3 text-center break-words">
          {userData?.err
            ? userData.message
            : `token: ${userData.principal.tokenValue}`}
        </div>
      </div>
    </main>
  );
}
```

![alt text](<../images/스크린샷 2024-03-09 오전 5.14.17.png>)
토큰이 비어있을 때는 `/user`에서 권한이 제한되지만
![alt text](<../images/스크린샷 2024-03-09 오전 5.15.23.png>)
다시 인증을 받고 요청했을 때는 유저 정보를 출력하는 것을 확인했습니다.

### JWT를 쿠키에 저장하는게 안전한가

JWT는 자체로 사용자에 대한 정보를 포함하고 있고 누구나 payload에 담겨있는 정보를 확인할 수 있습니다. 그래서 민감한 정보는 jwt에 담지 않는데 그래도 안정성이 떨어지는 쿠키에 어쨌든 누구나 확인 가능한 개인정보가 보관되고 있는 것은 불안합니다. 검색해보니 역시 쿠키에 저장하는 것은 추천하지 않고 session storage 혹은 local storage에 저장하는 것을 추천하고 있었습니다. 아마도 `Next Auth` 같은 전문적인 라이브러리는 쿠키보다는 더 안전한 방법으로 처리를 할 것 같습니다. 처음부터 스프링 시큐리티의 기능만 가지고 OAuth2를 실습하는게 목적이었기 때문에 이게 한계인 것 같습니다. Nextjs에서 브라우저 session, local storage를 다루는 기능이나 라이브러리가 있다면 미들웨어에서 매 요청마다 토큰을 확인하고 토큰이 있으면 세션에 저장한 뒤 바로 쿠키를 삭제하는 방법도 있는데 잠깐이나마 쿠키에 저장되어야 하기 때문에 확실한 해결법은 아니네요.

## 기타

### 커스터마이징 관련해서 기억할만한 패턴

아래 스프링 공식문서를 살펴보던 중 기존 서비스의 행동을 그대로 위임하면서 추가적인 행동만 확장하고 싶을 때 lambda식을 리턴하고 기존 서비스는 지역변수에 가두는 것을 확인했습니다. 마치 자바스크립트의 클로저처럼 작성된 코드가 신기해서 기록해놓습니다. 자바스크립트의 클로저는 내부 데이터를 조작할 수도 있으므로 동작 방식은 다르지만 객체를 생성하는 패턴은 같습니다.  
저는 커스터마이징한 클래스에서 다른 Bean이 필요해 질 것 같아서(UserEntity를 Repository에 저장하기 위한 Service 컴포넌트를 Autowired 하는 등) 이 패턴은 사용하지 않았습니다. 디폴트 클래스를 상속하고 `super.xxx()`를 호출하여 기존 행위를 위임했습니다.

https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html#oauth2login-advanced-map-authorities
![alt text](<../images/스크린샷 2024-03-08 오전 2.55.43.png>)
