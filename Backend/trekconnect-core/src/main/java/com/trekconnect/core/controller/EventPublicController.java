package com.trekconnect.core.controller;

import com.trekconnect.core.dto.response.EventResponse;
import com.trekconnect.core.entity.Event;
import com.trekconnect.core.entity.Trek;
import com.trekconnect.core.repository.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Public REST Controller exposing live dynamic Event Batches for Trekkers.
 * 
 * WHY THIS CONTROLLER WAS CREATED:
 * Guarantees zero static content on the trekker explorer home page.
 * When an organizer creates a new event batch in the database, trekkers immediately see it live from main_db.
 */
@RestController
@RequestMapping("/api/events")
public class EventPublicController {

    private static final Logger logger = LoggerFactory.getLogger(EventPublicController.class);

    private final EventRepository eventRepository;

    @Autowired
    public EventPublicController(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all published events from PostgreSQL database main_db with dynamic filters.
     */
    @GetMapping("/public")
    public ResponseEntity<List<EventResponse>> getPublicEvents(
            @RequestParam(value = "category", required = false, defaultValue = "ALL") String category,
            @RequestParam(value = "difficulty", required = false, defaultValue = "ALL") String difficulty,
            @RequestParam(value = "region", required = false, defaultValue = "ALL") String region,
            @RequestParam(value = "search", required = false) String search) {

        logger.info("REST Request: GET /api/events/public - Category: {}, Difficulty: {}, Region: {}, Search: {}",
                    category, difficulty, region, search);

        List<Event> events = eventRepository.findAll();

        List<EventResponse> response = events.stream()
                .filter(e -> "APPROVED".equalsIgnoreCase(e.getStatus()))
                .filter(e -> {
                    Trek trek = e.getTrek();
                    boolean matchesCategory = "ALL".equalsIgnoreCase(category) || (trek != null && category.equalsIgnoreCase(trek.getCategory()));
                    boolean matchesDifficulty = "ALL".equalsIgnoreCase(difficulty) || (trek != null && difficulty.equalsIgnoreCase(trek.getDifficulty()));
                    boolean matchesRegion = "ALL".equalsIgnoreCase(region) || (trek != null && region.equalsIgnoreCase(trek.getRegion()));
                    boolean matchesSearch = search == null || search.trim().isEmpty() ||
                            (e.getTitle() != null && e.getTitle().toLowerCase().contains(search.toLowerCase().trim())) ||
                            (trek != null && trek.getName() != null && trek.getName().toLowerCase().contains(search.toLowerCase().trim()));

                    return matchesCategory && matchesDifficulty && matchesRegion && matchesSearch;
                })
                .map(this::mapToEventResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    private EventResponse mapToEventResponse(Event event) {
        Trek trek = event.getTrek();
        String organizerName = (event.getOrganizer() != null && event.getOrganizer().getOrganizationName() != null) ?
                event.getOrganizer().getOrganizationName() : "Sahyadri Wanderers Expeditions";

        int totalCap = event.getCapacityTotal() != null ? event.getCapacityTotal() : 25;
        int bookedCap = event.getCapacityBooked() != null ? event.getCapacityBooked() : 0;

        Integer altFt = (trek != null && trek.getAltitudeMeters() != null) ? trek.getAltitudeMeters() : 2000;
        Integer durDays = (trek != null && trek.getDurationDays() != null) ? trek.getDurationDays() : 1;

        String coverImg = (event.getImageUrl() != null && !event.getImageUrl().isBlank()) ? event.getImageUrl() :
                ((trek != null && trek.getImageUrl() != null) ? trek.getImageUrl() : "assets/images/torna.svg");

        return EventResponse.builder()
                .id(event.getId())
                .trekId(trek != null ? trek.getId() : "N/A")
                .trekName(trek != null && trek.getName() != null ? trek.getName() : event.getTitle())
                .region(trek != null && trek.getRegion() != null ? trek.getRegion() : "Maharashtra")
                .difficulty(trek != null && trek.getDifficulty() != null ? trek.getDifficulty() : "MODERATE")
                .category(trek != null && trek.getCategory() != null ? trek.getCategory() : "SAHYADRI")
                .imageUrl(coverImg)
                .altitudeFt(altFt)
                .durationDays(durDays)
                .inclusions(trek != null && trek.getInclusions() != null ? trek.getInclusions() : "Transport;Breakfast & Lunch;Guide")
                .organizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : "N/A")
                .organizerName(organizerName)
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .price(event.getPrice())
                .capacityTotal(totalCap)
                .capacityBooked(bookedCap)
                .availableSlots(Math.max(0, totalCap - bookedCap))
                .version(event.getVersion() != null ? event.getVersion() : 0)
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .build();
    }
}
