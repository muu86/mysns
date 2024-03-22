package com.mj.mysns.api.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateUserPayload(

    @NotBlank
    String sub,

    @NotBlank
    String iss,

    @NotBlank
    String first,

    @NotBlank
    String last,

    @Email @NotBlank
    String email,

    boolean emailVerified,

    @NotBlank(message = "{NotBlank.CreateUserPayload.username}")
    String username,

    @Min(value = 0, message = "{Min.CreateUserPayload.babyAge}")
    int babyAge,

    @NotBlank
    String legalAddressCode

) {

}
