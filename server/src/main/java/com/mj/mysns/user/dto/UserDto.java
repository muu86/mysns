package com.mj.mysns.user.dto;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.user.entity.UserFile;
import java.util.List;
import lombok.Builder;

@Builder
public record UserDto(

    String username,

    String issuer,

    String subject,

    String first,

    String last,

    String email,

    Boolean emailVerified,

    Integer babyAge,

    String content,

    String legalAddressCode,

    List<LegalAddress> userAddresses,

    List<UserFile> userFiles
) {

}
