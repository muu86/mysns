package com.mj.mysns.location;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mj.mysns.location.entity.LegalAddress;
import com.mj.mysns.location.repository.AddressRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class LocationServiceTest {

    @MockBean
    AddressRepository addressRepository;

    @Autowired LocationService locationService;

    @Test
    void getAddressNear_returnLegalAddressesList() {
        String latitude = "37.59554172008826";
        String longitude = "126.96567699902982";
        int page = 1;
        int offset = 10;
        List<LegalAddress> addresses = new ArrayList<>();

        when(addressRepository.findLegalAddressNear(latitude, longitude, page, offset)).thenReturn(addresses);

        List<LegalAddress> results = locationService.getAddressNear(latitude, longitude, page,
            offset);
        assertEquals(results, addresses);
        verify(addressRepository, times(1)).findLegalAddressNear(latitude, longitude, page, offset);
    }

    @Test
    void getLegalAddresses_returnLegalAddressesList() {
        List<LegalAddress> addresses = new ArrayList<>();

        when(addressRepository.findAll()).thenReturn(addresses);

        List<LegalAddress> results = locationService.getLegalAddresses();

        assertEquals(results, addresses);
        verify(addressRepository, times(1)).findAll();
    }
}