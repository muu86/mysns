package com.mj.mysns.user.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserDto {

    String username;

    String first;

    String last;

    String email;

    Boolean oauth2;

    String provider;
}
