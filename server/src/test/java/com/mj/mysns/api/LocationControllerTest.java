package com.mj.mysns.api;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.when;

import com.mj.mysns.location.LocationService;
import com.mj.mysns.location.entity.LegalAddress;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class LocationControllerTest {

    @MockBean
    LocationService locationService;

    @Autowired
    WebTestClient client;

    List<LegalAddress> legalAddresses;

    @BeforeEach
    void setup() {
        List<LegalAddress> addresses = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
        addresses.add(LegalAddress.builder()
            .code(String.valueOf(i)).build());
        }
        this.legalAddresses = addresses;
    }

    @Test
    void getAllLegalAddresses_ok() {
        given(locationService.getLegalAddresses()).willReturn(this.legalAddresses);

        client.get().uri("/loc/legal")
            .exchange()
            .expectStatus().isOk()
            .expectBody().jsonPath("$.length()").isEqualTo(10);

        verify(locationService).getLegalAddresses();
    }

    @Test
    void getNearest_ok() {
        String latitude = "37.59554172008826";
        String longitude = "126.96567699902982";
        int page = 1;
        int offset = 10;
        List<LegalAddress> addresses = new ArrayList<>();

        when(locationService.getAddressNear(latitude, longitude, page, offset))
            .thenReturn(addresses);

        client.get().uri(uri -> uri
            .path("/loc/legal/near")
            .queryParam("latitude", latitude)
            .queryParam("longitude", longitude)
            .queryParam("page", page)
            .queryParam("offset", offset)
            .build())
            .exchange()
            .expectStatus().isOk();
    }
}