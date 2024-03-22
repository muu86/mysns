package com.mj.mysns.user.entity;

import com.mj.mysns.common.file.FileLocation;
import com.mj.mysns.location.entity.LegalAddress;
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
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"issuer", "subject"})
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    private String content;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<UserFile> userFiles;

    @OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<UserAddress> userAddresses;

    @Builder
    public User(String username, String first, String last, String email, Boolean emailVerified, String issuer,
        String subject, Integer babyAge, String content, List<UserFile> userFiles, List<UserAddress> userAddresses) {

        this.username = username;
        this.first = first;
        this.last = last;
        this.email = email;
        this.emailVerified = emailVerified;
        this.issuer = issuer;
        this.subject = subject;
        this.babyAge = babyAge;
        this.content = content;
        this.userFiles = Optional.ofNullable(userFiles).orElse(new ArrayList<>());
        this.userAddresses = Optional.ofNullable(userAddresses).orElse(new ArrayList<>());
    }

    public void addUserAddress(LegalAddress address) {
        UserAddress userAddress = new UserAddress(this, address);
        this.userAddresses.add(userAddress);
    }

    public void addUserFile(FileLocation fileLocation, Boolean active) {
        UserFile userFile = new UserFile(this, fileLocation, active);
        this.userFiles.add(userFile);
    }
}
