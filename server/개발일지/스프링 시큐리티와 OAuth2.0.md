# 스프링 시큐리티와 OAuth2.0

스프링 시큐리티는 **Client**, **Resource Server** 모듈을 제공한다.
**Authorization Server** 프로젝트는 스프링 시큐리티 프로젝트와 분리되어 별도로 제공된다.

## Client 모듈
인가서버 및 리소스 서버와의 통신을 담당하는 클라이언트의 기능을 **필터** 기반으로 구현한 모듈.
- OAuth2.0 Login
  - 애플리케이션의 사용자를 외부 OAuth 2.0 Provider 나 OpenID Connect 1.0 Provider 계정으로 로그인할 수 있는 기능을 제공한다.
  - Authorization Code 방식을 사용한다.
- OAuth2.0 Client
  - 인가 서버의 권한 부여 유형에 따른 엔드포인트와 직접 통신할 수 있는 API를 제공한다.
  - Client Credentials, Resource Owner Password Credentials, Refresh Token
  - 리소스 서버의 자원 접근에 대한 연동 모듈을 구현할 수 있다.


### 환경 설정 흐름
- application.yml
  - application.yml 설정 파일에 클라이언트 설정과 인가서버 엔드포인트 설정을 한다.
- OAuth2ClientProperties
  - 초기화가 진행되면 application.yml에 있는 클라이언트 및 인가서버 정보가 OAuth2ClientPorperties
- ClientRegistration
  - 바인딩 된 속성 정보가 ClientRegistration 클래스의 필드에 저장됨.
- OAuth2Client
  - ClientRegistration 클래스를 참조하여 매개변수 구성.

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          keycloak:
            clientId: oauth2-client-app
            clientSecret: qcWhQDyT81IdRxHd1Q9QjQGz3PnpI41A
            clientName: oauth2-client-app
            redirectUri: http://localhost:8080/login/oauth2/code/keycloak
            authorizationGrantType: authorization_code
            clientAuthenticationMethod: client_secret_basic
            scope:
              - openid
              - profile
              - email
        provider:
          keycloak:
            authorizationUri: http://localhost:3333/realms/master/protocol/openid-connect/auth
            tokenUri: http://localhost:3333/realms/master/protocol/openid-connect/token
            issuerUri: http://localhost:3333/realms/master
            userInfoUri: http://localhost:3333/realms/master/protocol/openid-connect/userinfo
            jwkSetUri: http://localhost:3333/realms/master/protocol/openid-connect/certs
            userNameAttribute: preferred_username
```

    jwtSetUri ?
    인가서버에서 JSON 웹 키 (JWK) set을 가져올 때 사용할 uri.
    이 key set엔 ID Token의 JWT를 검증할 때 사용할 암호키가 있으며,
    UserInfo 응답을 검증할 때도 사용할 수 있다.
    

### ClientRegistration
OpenID Connect Provider의 설정 엔드포인트나 인가 서버의 메타데이터 엔드포인트를 찾아 초기화할 수 있다.
```java
ClientRegistration clientRegistration = ClientRegistrations.fromIssuerLocation("http://idp.example.issuer").build();
// 위 코드는 200 응답을 받을 때까지 https://idp.example.com/issuer/.well-known/openid-configuration, https://idp.example.com/.well-known/oauth-authorization-server 에 차례대로 질의해본다
```

### CommonOAuth2Provider
구글, 깃허브, 페이북, Okta 4개의 글로벌 서비스 제공자는 기본으로 제공된다.
```java
public enum CommonOAuth2Provider {
    GOOGLE {
        @Override
        public Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId,
                ClientAuthenticationMethod.CLIENT_SECRET_BASIC, DEFAULT_REDIRECT_URL);
            builder.scope("openid", "profile", "email");
            builder.authorizationUri("https://accounts.google.com/o/oauth2/v2/auth");
            builder.tokenUri("https://www.googleapis.com/oauth2/v4/token");
            builder.jwkSetUri("https://www.googleapis.com/oauth2/v3/certs");
            builder.issuerUri("https://accounts.google.com");
            builder.userInfoUri("https://www.googleapis.com/oauth2/v3/userinfo");
            builder.userNameAttributeName(IdTokenClaimNames.SUB);
            builder.clientName("Google");
            return builder;
        }

    }
}
```

### ClientRegistrationRepository
OAuth2.0 과 OpenID Connect 1.0 의 ClientRegistration 저장소

### OAuth2AuthorizationRequestRedirectFilter
클라이언트가 사용자의 브라우저를 통해 인가 서버의 권한 부여 엔드포인트로 리다이렉션하여 Authoriztion Code Grant Flow를 시작한다.
request url이 **/oauth2/authorization/{registrationId}** 에 매치되면 필터가 작동한다. 

### Authorization Code 요청하고 리다이렉트되는 흐름
- 사용자 Request -> oauth2/authorization/{registrationId}
- OAuth2AuthorizationRequestRedirectFilter 발동
- DefaultOAuth2AuthorizationRequestResolver
  - 웹 요청에 대해 OAuth2AuthorizationRequest 객체를 완성
- OAuth2AuthorizationRequest 객체
  - 인가 서버에 코드를 요청하기 위한 정보들을 담고 있음.
- OAuth2AuthorizationRequestRepository
  - 인가 요청을 시작한 시점부터 인가 요청을 받는 시점까지(리다이렉트) OAuth2AuthorizationRequest를 유지함. 기본적으로 세션에 저장.

