package com.mj.mysns.user.dto;

import lombok.Builder;

@Builder
public record UserDto(
    String issuer,

    String subject,

    String first,

    String last,

    String email,

    Boolean emailVerified,

    String username,

    Integer babyAge,

    String legalAddressCode

) {

}
