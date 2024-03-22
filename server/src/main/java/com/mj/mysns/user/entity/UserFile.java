package com.mj.mysns.user.entity;

import com.mj.mysns.common.file.FileLocation;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter

@Entity
public class UserFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    private User user;

    @Embedded
    private FileLocation fileLocation;

    private Boolean active;

    @Builder
    public UserFile(User user, FileLocation fileLocation, Boolean active) {
        this.user = user;
        this.fileLocation = fileLocation;
        this.active = active;
    }
}
