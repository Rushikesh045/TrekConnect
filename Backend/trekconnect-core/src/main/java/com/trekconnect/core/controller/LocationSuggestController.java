package com.trekconnect.core.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller exposing Free Location Autocomplete Suggestions.
 * 
 * WHY THIS CONTROLLER WAS CREATED:
 * Provides dynamic location suggestions as organizers type into the location search input box.
 */
@RestController
@RequestMapping("/api/locations")
public class LocationSuggestController {

    private static final Logger logger = LoggerFactory.getLogger(LocationSuggestController.class);

    private static final List<String> SUGGESTED_LOCATIONS = Arrays.asList(
            "Velhe, Pune District, Maharashtra",
            "Lonavala, Pune District, Maharashtra",
            "Karjat, Raigad District, Maharashtra",
            "Khireshwar, Ahmednagar, Maharashtra",
            "Bari Village, Igatpuri, Nashik, Maharashtra",
            "Bhira, Kolad, Raigad, Maharashtra",
            "Bhandardara, Ahmednagar, Maharashtra",
            "Mahabaleshwar, Satara District, Maharashtra",
            "Wai, Satara District, Maharashtra",
            "Matheran, Raigad District, Maharashtra",
            "Kasara, Thane District, Maharashtra",
            "Sankri, Uttarkashi, Uttarakhand",
            "Dehradun, Uttarakhand",
            "Manali, Kullu District, Himachal Pradesh",
            "Kasol, Parvati Valley, Himachal Pradesh",
            "Leh, Ladakh, Jammu & Kashmir",
            "Coorg, Western Ghats, Karnataka",
            "Wayanad, Western Ghats, Kerala"
    );

    @GetMapping("/suggest")
    public ResponseEntity<List<String>> suggestLocations(@RequestParam(value = "q", required = false) String query) {
        logger.info("REST Request: GET /api/locations/suggest?q={}", query);

        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(SUGGESTED_LOCATIONS.stream().limit(6).collect(Collectors.toList()));
        }

        String lowerQuery = query.toLowerCase().trim();
        List<String> matches = SUGGESTED_LOCATIONS.stream()
                .filter(loc -> loc.toLowerCase().contains(lowerQuery))
                .limit(8)
                .collect(Collectors.toList());

        return ResponseEntity.ok(matches);
    }
}
