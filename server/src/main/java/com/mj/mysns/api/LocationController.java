package com.mj.mysns.api;

import com.mj.mysns.api.result.LegalAddressResult;
import com.mj.mysns.location.LocationService;
import com.mj.mysns.location.entity.LegalAddress;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/loc")
public class LocationController {

    private final LocationService locationService;

    @GetMapping("/legal")
    public ResponseEntity<List<LegalAddressResult>> getLegalAddresses() {
        List<LegalAddress> legalAddresses = locationService.getLegalAddresses();
        List<LegalAddressResult> results = legalAddresses.stream()
            .map(a -> LegalAddressResult.builder()
                .code(a.getCode())
                .sido(a.getSido())
                .gungu(a.getGungu())
                .eupmyundong(a.getEupmyundong())
                .li(a.getLi())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    @GetMapping(path = "/legal/near")
    public ResponseEntity<List<LegalAddressResult>> getLegalAddressesNear(
        @RequestParam("latitude") String latitude,
        @RequestParam("longitude") String longitude,
        @RequestParam(value = "page", defaultValue = "5") int page,
        @RequestParam(value = "offset", defaultValue = "0") int offset) {

        List<LegalAddress> nearestAddress = locationService.getAddressNear(latitude, longitude, page, offset);
        List<LegalAddressResult> results = nearestAddress.stream()
            .map(a -> LegalAddressResult.builder()
                .code(a.getCode())
                .sido(a.getSido())
                .gungu(a.getGungu())
                .eupmyundong(a.getEupmyundong())
                .li(a.getLi())
                .build())
            .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

}
