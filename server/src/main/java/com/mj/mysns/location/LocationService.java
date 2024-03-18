package com.mj.mysns.location;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor

@Service
public class LocationService {

    private final AddressRepository addressRepository;

    public List<LegalAddress> getAddressNear(String latitude, String longitude, int page,
        int offset) {
        return addressRepository.findLegalAddressNear(latitude, longitude, page, offset);
    }

}
