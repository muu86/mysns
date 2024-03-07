package com.mj.mysns.domain.user.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    // oauth2 인증 사용자인지
    // 일단 oauth2만 제공하고 기본 회원가입은 개발하지 않을 예정
    private Boolean oauth2;

    private String provider;

    public static User create(String username, String firstName, String lastName, String email,
        Boolean oauth2, String provider) {
        User user = new User();
        user.username = username;
        user.firstName = firstName;
        user.lastName = lastName;
        user.email = email;
        user.oauth2 = oauth2;
        user.provider = provider;
        return user;
    }
}
