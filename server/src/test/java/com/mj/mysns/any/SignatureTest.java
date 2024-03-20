package com.mj.mysns.any;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKMatcher.Builder;
import com.nimbusds.jose.jwk.JWKSelector;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyOperation;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.OctetSequenceKeyGenerator;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.Test;

public class SignatureTest {

    final String message = "테스트용 메세지입니다~!!";

    @Test
    void signature() throws Exception {

        // 비대칭키 생성
        KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");
        gen.initialize(2048);
        KeyPair keyPair = gen.generateKeyPair();

        // 개인키로 서명
        byte[] digest = message.getBytes("utf-8");
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

    @Test
    void hamc() throws Exception {
        String algorithm = "HmacMD5";
        byte[] secret = "test secret".getBytes("utf-8");

        // secret key 생성
        SecretKeySpec secretKey = new SecretKeySpec(secret, algorithm);

        Mac mac = Mac.getInstance(algorithm);
        mac.init(secretKey);
        // 비밀키로 암호화한 해쉬
        byte[] hash = mac.doFinal(message.getBytes());

        String encoded = Base64.getEncoder().encodeToString(hash);

        System.out.println(encoded);
    }

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
        JWKSource<SecurityContext> jwkSource = ((jwkSelector, context) -> jwkSelector.select(
            jwkSet));
        JWKSelector rsaSelector = new JWKSelector(new Builder().keyID("rsa-kid").build());
        List<JWK> jwks = jwkSource.get(rsaSelector, null);
        JWK jwk = jwks.getFirst();

        assertEquals("rsa-kid", jwk.getKeyID());
    }
}
