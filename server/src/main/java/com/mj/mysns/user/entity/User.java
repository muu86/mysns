package com.mj.mysns.user.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;
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

    @Column(unique = true)
    @Setter
    private String username;

    private String first;

    private String last;

    private String email;

    private Boolean emailVerified;

    private String issuer;

    private String subject;

    private Integer babyAge;

    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private List<UserLegalAddress> userAddress;

    @Builder
    public User(String username, String first, String last, String email, Boolean emailVerified, String issuer,
        String subject, Integer babyAge, List<UserLegalAddress> userAddress) {
        this.username = username;
        this.first = first;
        this.last = last;
        this.email = email;
        this.emailVerified = emailVerified;
        this.issuer = issuer;
        this.subject = subject;
        this.babyAge = babyAge;
        this.userAddress = userAddress == null ? new ArrayList<UserLegalAddress>() : userAddress;
    }
}
