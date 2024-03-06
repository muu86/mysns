# Oauth 2.0

## Oauth 2.0 이란

**권한 부여**를 위해 광범위하게 사용되는 공개 표준 프레임워크로 
사용자에게 권한을 위임받은 서드 파티 클라이언트 애플리케이션이 
사용자를 대신해 보안 리소스에 접근할 수 있게 한다.

## Roles
- 자원 소유자(Resource Owner)
  - 보호된 자원에 대한 접근 권한을 부여할 수 있는 주체
  - 클라이언트는 먼저 사용자의 허가를 받아야 한다.
- 자원 서버(Resource Server)
  - 보호 대상 자원에 대한 API를 제공하는 서버
  - 액세스 토큰을 수락 및 검증할 수 있어야 하며 권한 체계에 따라 요청을 승인할 수 있어야 한다.
- 인가 서버(Authorization Server)
  -  클라이언트의 권한 부여 요청을 승인하거나 거부하는 서버
  - 사용자가 클라이언트에게 권한 부여 요청을 승인한 후 access token을 클라이언트에게 부여하는 역할
- 클라이언트(Client)
  - 사용자를 대신하여 사용자의 리소스에 접근하는 애플리케이션
  - 사용자를 권한 부여 서버로 안내하거나 사용자의 상호 작용 없이 권한 부여 서버로부터 직접 권한을 얻을 수 있다.

## Client Types
클라이언트를 인증 서버에 등록할 때 클라이언트 아이디와 클라이언트 패스워드를 받는다.
- 기밀 클라이언트(Confidential Clients)
  - client_secret 의 기밀성을 유지할 수 있는 클라이언트
  - 사용자가 소스코드에 접근할 수 없는 앱, 서버 측 언어로 작성됨
  - 웹 앱
- 공개 클라이언트(Public Clients)
  - 기밀성 유지할 수 없음 
  - 브라우저에서 실행되는 JavaScript 애플리케이션, 모바일 앱 등
  - 개발자 콘솔 등으로 기밀 정보를 추출 가능
  - 이러한 클라이언트에는 sercret이 사용되지 않는다.
  - implicit grant flow 로

## Token Types
- Access Token
  - 클라이언트에서 리소스에 접근하기 위해 사용하는 일종의 자격증명
  - 일반적으로 JWT 형식을 취한다.
- Refresh Token
  - Access Token 이 만료된 후 새 토큰을 발급받기 위한 토큰
  - 인증 서버는 Refresh Token의 유효성을 검사, 새 Access Token 발급
- ID Token
- Authorization Code
  - 권한 부여 코드 흐름에서 사용, 클라이언트가 Access Token 과 교환할 임시 코드

## Access Token 의 유형
- 식별자 타입
  - 리소스 서버가 클라이언트로부터 받은 access token를 다시 검증하기 위하여 인가서버와 통신해야 한다.
- 자체 포함 타입(Self-contained Type)
  - JWT 토큰 형식으로 발급된다.
  - 리소스 서버가 검증 키 등의 핵심 자료를 알고 있다면 인가 서버와 통신할 필요없이 토큰 유효성을 검사할 수 있다.
  - JWT 는 개인키로 서명되고 공개키로 검증이 가능하다.

## 권한 부여 유형
- Authorization Code Grant Type
- Implicit Grant Type
- Resource Owner Password Credentials Grant Type
- Client Credentials Grant Type

### Authorization Code Grant Type
흐름
- 사용자가 애플리케이션 승인
- 인가서버는 redirect_uri 로 임시 **grant code**를 담아 클라이언트에게 리다이렉트
- 클라이언트는 code 와 access token 을 교환하고자 인가 서버 다시 호출, 이 때 code 와 client_id, client_secret 필요
- 인가서버는 **access token** 발급, **refresh token**을 발급할 수도 있다.
- 클라이언트는 access token을 사용해 리소스 서버가 공개하는 보안 api에 요청
- 리소스 서버는 access token 검증, 요청 처리

**grant code** 요청 시 매개 변수
GET /authorize?
- response_type=code(필수)
- client_id(필수)
- redirect_uri
- scope
- state

**access token** 교환 요청 시 매개변수
POST /token?
- grant_type=authorization_code(필수)
- code(필수)
- redirect_uri(필수, 코드 요청 시 포함한 경우)
- client_id(필수)
- client_secret(필수)