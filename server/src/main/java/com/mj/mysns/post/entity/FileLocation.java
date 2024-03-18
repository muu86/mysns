package com.mj.mysns.post.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor

@Embeddable
public class FileLocation {

    @Enumerated(EnumType.STRING)
    private FileLocationType fileLocationType;

    private String location;

    public enum FileLocationType {
        S3
    }
}
