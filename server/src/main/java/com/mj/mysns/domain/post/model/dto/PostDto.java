package com.mj.mysns.domain.post.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

public record PostDto(
    @NotNull String content) {
//    Long userId;

}
