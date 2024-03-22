package com.mj.mysns.common.file;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor

@Embeddable
public class FileLocation {

    @Enumerated(EnumType.STRING)
    private FileLocationType fileLocationType;

    private String location;

    public enum FileLocationType {
        S3
    }
}
