package com.mj.mysns.domain.user.model.dto;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class UserDto {

    String userName;

    String firstName;

    String lastName;

    String email;

    Boolean oauth2;

    String provider;
}
