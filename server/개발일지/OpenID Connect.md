# OpenID Connect

Oauth2.0은 Oauth2.0 프로토콜 위에 구축된 ID 계층. Oauth2.0 기반의 **인증(Authentication)** 프로토콜이다.
반면, Oauth2.0은 **인가(Authorization)**(권한 부여)를 위해 사용되는 프로토콜.

scope 지정 시 'openid'를 포함하면 OpenID Connect 사용이 가능하며,
인증에 대한 정보는 **ID Token**이라고 하는 JWT로 반환된다.

## ID Token
- 사용자가 **인증**되었음을 증명, access token과 함께 클라이언트에게 전달된다.
- JWT 는 헤더, 페이로드, 서명으로 구성
- 개인키로 발급자가 서명, 토큰의 출처 보장, 변조되지 않았음을 보장
- 클라이언트는 공개키로 ID Token을 검증 및 유효성 검사
- 클라이언트는 사용자명, 이메일을 활용, 인증 관리를 할 수 있다.

### ID Token 과 Access Token의 차이점
ID Token은 API 요청에 사용되어서는 안된다. 사용자의 신원확인을 위해 사용하는 것.
Access Token은 인증을 위해 사용되어서는 안된다. 리소스에 접근하기 위해 사용.
