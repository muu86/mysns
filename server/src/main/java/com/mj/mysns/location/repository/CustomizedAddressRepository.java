package com.mj.mysns.location.repository;

import com.mj.mysns.location.entity.LegalAddress;
import java.util.List;

public interface CustomizedAddressRepository {

    List<LegalAddress> findLegalAddressNear(String latitude, String longitude, int page,
        int offset);
}
