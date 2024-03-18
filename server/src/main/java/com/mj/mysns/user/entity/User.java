package com.mj.mysns.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String first;

    private String last;

    private String email;

    // oauth2 인증 사용자인지
    // 일단 oauth2만 제공하고 기본 회원가입은 개발하지 않을 예정
    private Boolean oauth2;

    private String provider;

    @Builder
    public User(String username, String first, String last, String email, Boolean oauth2,
        String provider) {
        this.username = username;
        this.first = first;
        this.last = last;
        this.email = email;
        this.oauth2 = oauth2;
        this.provider = provider;
    }
}
