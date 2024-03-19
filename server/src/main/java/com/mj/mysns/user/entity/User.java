package com.mj.mysns.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"issuer", "subject"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = true)
    @Setter
    private String username;

    private String first;

    private String last;

    private String email;

    private Boolean emailVerified;

    private String issuer;

    private String subject;

    @Builder
    public User(String username, String first, String last, String email, Boolean emailVerified, String issuer,
        String subject) {
        this.username = username;
        this.first = first;
        this.last = last;
        this.email = email;
        this.emailVerified = emailVerified;
        this.issuer = issuer;
        this.subject = subject;
    }
}
