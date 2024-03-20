package com.mj.mysns.api.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;

public record CreateUserPayload(

    String sub,

    String iss,

    String first,

    String last,

    @Email
    String email,

    boolean emailVerified,

    String username,

    @Min(value = 0)
    int babyAge,

    String legalAddressCode

) {

}
