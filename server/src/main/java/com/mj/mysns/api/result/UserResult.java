package com.mj.mysns.api.result;

import lombok.Builder;

@Builder
public record UserResult(
    String username
) {

}
