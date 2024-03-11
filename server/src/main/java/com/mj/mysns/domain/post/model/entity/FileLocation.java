package com.mj.mysns.domain.post.model.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FileLocation {

    @Enumerated(EnumType.STRING)
    private FileLocationType fileLocationType;

    private String location;

    public enum FileLocationType {
        S3
    }
}
