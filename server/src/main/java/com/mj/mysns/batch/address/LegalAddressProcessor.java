package com.mj.mysns.batch.address;

import com.mj.mysns.location.entity.LegalAddress;
import org.springframework.batch.item.ItemProcessor;

public class LegalAddressProcessor implements ItemProcessor<LegalAddress, LegalAddress> {

    // 주소 데이터에서 삭제된 데이터인 경우 제외시킨다.
    @Override
    public LegalAddress process(LegalAddress item) {
        if (item.getDeletedAt() != null) return null;

        return item;
    }
}
