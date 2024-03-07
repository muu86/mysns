## 암호화 알고리즘의 분류

### 대칭 암호화 (Symmetric Encryption)

- 암호화, 복호화에 같은 키를 사용
- MAC, HMAC

### 비대칭 암호화 (Asymmetric Encryption)

- 공개키(외부에 공개)와 개인키(메세지 송신자만 보유)
- 공개키로 암호화하면 개인키로 복호화
- 개인키로 암호화하면 공개키로 암호화
- RSA

### 해쉬 함수

- 임의의 길이의 데이터를 고정된 길이의 데이터로 변환하는 해쉬함수를 사용
- 단방향 암호화, 입력 데이터를 해시 값으로 변환하는 것은 가능하지만, 해시 값을 원래 데이터로 역산하는 것은 거의 불가능.
- SHA-256, MD5, SHA-1

## JWT

구조

- Header
  - 토큰유형(JWT), 서명 알고리즘 지정
- Paylaod
  - 토큰에 포함할 내용(claims)
- Signature

````js
    HMAC_SHA256(
      secret,
      base64urlEncoding(header) + '.' + base64urlEncoding(payload)
    );
    ```
````

JWT = Base64(header).Base64(payload).Base64(signature)!!

## JWK

암호화 키를 저장하는 방식으로 인가서버에서 발행하는 JWT 토큰의 암호화 및 서명에 필요한 암호화 키의 다양한 정보를 담은 JSON 객체 표준이다.
