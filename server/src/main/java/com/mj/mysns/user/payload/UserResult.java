package com.mj.mysns.user.payload;

import lombok.Builder;

@Builder
public record UserResult(
    String username
) {

}
