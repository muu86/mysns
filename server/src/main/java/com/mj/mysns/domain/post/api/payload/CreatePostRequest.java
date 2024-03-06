package com.mj.mysns.domain.post.api.payload;

import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(

    @NotNull
    String content

//    Long userId,
) {
}
