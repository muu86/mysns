package com.mj.mysns.api.payload;

import jakarta.validation.constraints.NotNull;

public record CreatePostRequest(

    @NotNull
    String content

//    Long userId,
) {
}
