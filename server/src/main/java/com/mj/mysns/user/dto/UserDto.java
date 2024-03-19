package com.mj.mysns.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
    String username,

    String first,

    String last,

    String email,

    Boolean emailVerified,

    String issuer,

    String subject
) {

}
