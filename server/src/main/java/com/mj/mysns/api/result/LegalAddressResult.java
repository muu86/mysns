package com.mj.mysns.api.result;

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
