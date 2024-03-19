package com.mj.mysns.location.api.payload;

import lombok.Builder;

@Builder
public record LegalAddressResult(
    String code,

    String sido,

    String gungu,

    String eupmyundong,

    String li

) {

}
