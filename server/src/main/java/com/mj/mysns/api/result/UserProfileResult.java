package com.mj.mysns.api.result;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.user.entity.UserFile;
import java.util.List;
import lombok.Builder;

@Builder
public record UserProfileResult(
    String username,

    Integer babyAge,

    String content,

    List<UserFile> userFiles,

    List<LegalAddress> userAddresses
) {

}
