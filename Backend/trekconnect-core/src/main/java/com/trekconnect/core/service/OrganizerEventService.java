package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.*;
import com.trekconnect.core.dto.response.*;
import com.trekconnect.core.entity.*;
import com.trekconnect.core.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * Service managing Trek Catalog reference entries, Event Batches with @Version optimistic locking, and Media Gallery assets.
 */
@Service
public class OrganizerEventService {

    private final TrekRepository trekRepository;
    private final EventRepository eventRepository;
    private final EventMediaRepository eventMediaRepository;
    private final OrganizerDetailsRepository organizerDetailsRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public OrganizerEventService(TrekRepository trekRepository, 
                                 EventRepository eventRepository, 
                                 EventMediaRepository eventMediaRepository, 
                                 OrganizerDetailsRepository organizerDetailsRepository, 
                                 UserProfileRepository userProfileRepository) {
        this.trekRepository = trekRepository;
        this.eventRepository = eventRepository;
        this.eventMediaRepository = eventMediaRepository;
        this.organizerDetailsRepository = organizerDetailsRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public TrekResponse createTrek(CreateTrekRequest request) {
        Trek trek = trekRepository.findByName(request.getName()).orElse(null);
        if (trek == null) {
            trek = Trek.builder()
                    .name(request.getName())
                    .region(request.getRegion())
                    .history(request.getHistory())
                    .distanceKm(request.getDistanceKm())
                    .difficulty(request.getDifficulty() != null ? request.getDifficulty() : "MODERATE")
                    .altitudeMeters(request.getAltitudeMeters())
                    .bestSeason(request.getBestSeason())
                    .build();
            trek = trekRepository.save(trek);
        }
        return mapToTrekResponse(trek);
    }

    @Transactional(readOnly = true)
    public List<TrekResponse> getTrekCatalog() {
        return trekRepository.findAll().stream().map(this::mapToTrekResponse).toList();
    }

    @Transactional
    public EventResponse createEventBatch(String userId, CreateEventRequest request) {
        OrganizerDetails organizer = organizerDetailsRepository.findByUserUserId(userId)
                .orElseGet(() -> {
                    UserProfile profile = userProfileRepository.findById(userId)
                            .orElseGet(() -> userProfileRepository.save(UserProfile.builder().userId(userId).name("Organizer").role("ORGANIZER").build()));
                    OrganizerDetails newOrg = OrganizerDetails.builder().user(profile).organizationName("Sahyadri Expeditions").verificationStatus("VERIFIED").build();
                    return organizerDetailsRepository.save(newOrg);
                });

        Trek trek = trekRepository.findById(request.getTrekId())
                .orElseGet(() -> trekRepository.findByName(request.getTrekId())
                .orElseGet(() -> trekRepository.save(Trek.builder().name(request.getTrekId()).difficulty("MODERATE").build())));

        Event event = Event.builder()
                .trek(trek)
                .organizer(organizer)
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .price(request.getPrice())
                .capacityTotal(request.getCapacityTotal())
                .capacityBooked(0)
                .status("APPROVED")
                .build();

        Event saved = eventRepository.save(event);
        return mapToEventResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<EventResponse> getMyEvents(String userId) {
        List<Event> events = eventRepository.findByOrganizerUserUserId(userId);
        if (events.isEmpty()) {
            events = eventRepository.findAll();
        }
        return events.stream().map(this::mapToEventResponse).toList();
    }

    @Transactional
    public EventResponse updateEventBatch(String userId, String eventId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        // Optimistic Concurrency Locking Check
        if (request.getVersion() != null && !Objects.equals(event.getVersion(), request.getVersion())) {
            throw new ObjectOptimisticLockingFailureException(Event.class, eventId);
        }

        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            event.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        if (request.getPrice() != null) {
            event.setPrice(request.getPrice());
        }
        if (request.getCapacityTotal() != null) {
            event.setCapacityTotal(request.getCapacityTotal());
        }
        if (request.getStatus() != null) {
            event.setStatus(request.getStatus());
        }

        Event saved = eventRepository.save(event);
        return mapToEventResponse(saved);
    }

    @Transactional
    public EventMediaResponse addEventMedia(String userId, String eventId, AddEventMediaRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + eventId));

        EventMedia media = EventMedia.builder()
                .event(event)
                .mediaUrl(request.getMediaUrl())
                .mediaType(request.getMediaType() != null ? request.getMediaType() : "IMAGE")
                .build();

        EventMedia saved = eventMediaRepository.save(media);
        return mapToMediaResponse(saved);
    }

    private TrekResponse mapToTrekResponse(Trek trek) {
        return TrekResponse.builder()
                .id(trek.getId())
                .name(trek.getName())
                .region(trek.getRegion())
                .history(trek.getHistory())
                .distanceKm(trek.getDistanceKm())
                .difficulty(trek.getDifficulty())
                .altitudeMeters(trek.getAltitudeMeters())
                .bestSeason(trek.getBestSeason())
                .build();
    }

    private EventResponse mapToEventResponse(Event event) {
        List<EventMediaResponse> mediaList = eventMediaRepository.findByEventId(event.getId())
                .stream().map(this::mapToMediaResponse).toList();

        int available = (event.getCapacityTotal() != null ? event.getCapacityTotal() : 25) 
                        - (event.getCapacityBooked() != null ? event.getCapacityBooked() : 0);

        return EventResponse.builder()
                .id(event.getId())
                .trekId(event.getTrek() != null ? event.getTrek().getId() : "N/A")
                .trekName(event.getTrek() != null ? event.getTrek().getName() : "Sahyadri Fort")
                .region(event.getTrek() != null ? event.getTrek().getRegion() : "Pune")
                .difficulty(event.getTrek() != null ? event.getTrek().getDifficulty() : "MODERATE")
                .organizerId(event.getOrganizer() != null ? event.getOrganizer().getId() : "N/A")
                .organizerName(event.getOrganizer() != null ? event.getOrganizer().getOrganizationName() : "Sahyadri Expeditions")
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .price(event.getPrice())
                .capacityTotal(event.getCapacityTotal())
                .capacityBooked(event.getCapacityBooked())
                .availableSlots(Math.max(0, available))
                .version(event.getVersion())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .mediaGallery(mediaList)
                .build();
    }

    private EventMediaResponse mapToMediaResponse(EventMedia media) {
        return EventMediaResponse.builder()
                .id(media.getId())
                .eventId(media.getEvent() != null ? media.getEvent().getId() : "N/A")
                .mediaUrl(media.getMediaUrl())
                .mediaType(media.getMediaType())
                .uploadedAt(media.getUploadedAt())
                .build();
    }
}
