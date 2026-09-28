package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.*;
import com.trekconnect.core.dto.response.*;
import com.trekconnect.core.service.OrganizerEventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Organizer Trek Catalog & Event Batch Management endpoints.
 */
@RestController
@RequestMapping("/api/organizer")
@PreAuthorize("hasAnyRole('ORGANIZER', 'ADMIN')")
public class OrganizerEventController {

    private final OrganizerEventService organizerEventService;

    @Autowired
    public OrganizerEventController(OrganizerEventService organizerEventService) {
        this.organizerEventService = organizerEventService;
    }

    @PostMapping("/treks")
    public ResponseEntity<TrekResponse> createTrek(@Valid @RequestBody CreateTrekRequest request) {
        TrekResponse response = organizerEventService.createTrek(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/treks")
    public ResponseEntity<List<TrekResponse>> getTrekCatalog() {
        List<TrekResponse> response = organizerEventService.getTrekCatalog();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/events")
    public ResponseEntity<EventResponse> createEventBatch(
            Authentication authentication,
            @Valid @RequestBody CreateEventRequest request) {
        String userId = (String) authentication.getPrincipal();
        EventResponse response = organizerEventService.createEventBatch(userId, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/my-events")
    public ResponseEntity<List<EventResponse>> getMyEvents(Authentication authentication) {
        String userId = (String) authentication.getPrincipal();
        List<EventResponse> response = organizerEventService.getMyEvents(userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/events/{id}")
    public ResponseEntity<EventResponse> updateEventBatch(
            Authentication authentication,
            @PathVariable("id") String id,
            @Valid @RequestBody UpdateEventRequest request) {
        String userId = (String) authentication.getPrincipal();
        EventResponse response = organizerEventService.updateEventBatch(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/events/{id}/media")
    public ResponseEntity<EventMediaResponse> addEventMedia(
            Authentication authentication,
            @PathVariable("id") String id,
            @Valid @RequestBody AddEventMediaRequest request) {
        String userId = (String) authentication.getPrincipal();
        EventMediaResponse response = organizerEventService.addEventMedia(userId, id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEventBatch(
            Authentication authentication,
            @PathVariable("id") String id) {
        String userId = (String) authentication.getPrincipal();
        organizerEventService.deleteEventBatch(userId, id);
        return ResponseEntity.noContent().build();
    }
}
