package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.OrganizerApplicationRequest;
import com.trekconnect.core.dto.response.OrganizerApplicationResponse;
import com.trekconnect.core.service.OrganizerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller exposing Organizer Application onboarding endpoints.
 */
@RestController
@RequestMapping("/api/organizers")
public class OrganizerController {

    private final OrganizerService organizerService;

    @Autowired
    public OrganizerController(OrganizerService organizerService) {
        this.organizerService = organizerService;
    }

    @PostMapping("/apply")
    public ResponseEntity<OrganizerApplicationResponse> applyForOrganizer(
            Authentication authentication,
            @Valid @RequestBody OrganizerApplicationRequest request) {
        String userId = (String) authentication.getPrincipal();
        OrganizerApplicationResponse response = organizerService.applyForOrganizer(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-status")
    public ResponseEntity<OrganizerApplicationResponse> getMyApplicationStatus(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        OrganizerApplicationResponse response = organizerService.getApplicationStatus(userId);
        return ResponseEntity.ok(response);
    }
}
