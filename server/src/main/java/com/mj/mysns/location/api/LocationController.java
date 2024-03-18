package com.mj.mysns.location.api;

import com.mj.mysns.location.LocationService;
import com.mj.mysns.location.entity.LegalAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @GetMapping(path = "/near")
    public ResponseEntity<List<Map<String, String>>> getNearest(
        @RequestParam("latitude") String latitude,
        @RequestParam("longitude") String longitude,
        @RequestParam(value = "page", defaultValue = "5") int page,
        @RequestParam(value = "offset", defaultValue = "0") int offset) {

        List<LegalAddress> nearestAddress = locationService.getAddressNear(latitude, longitude, page, offset);
        List<Map<String, String>> results = new ArrayList<>();
        for (LegalAddress address : nearestAddress) {
            Map<String, String> result = new HashMap<>();
            result.put("name", address.getEupmyundong());
            results.add(result);
        }
        return ResponseEntity.ok(results);
    }
}
