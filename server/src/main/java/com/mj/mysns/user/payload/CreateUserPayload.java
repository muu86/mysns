package com.mj.mysns.user.payload;

public record CreateUserPayload(

    String sub,

    String iss,

    String first,

    String last,

    String email,

    Boolean emailVerified

) {

}
