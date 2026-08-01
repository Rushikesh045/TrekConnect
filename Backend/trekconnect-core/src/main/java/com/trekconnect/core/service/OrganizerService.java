package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.OrganizerApplicationRequest;
import com.trekconnect.core.dto.response.OrganizerApplicationResponse;
import com.trekconnect.core.entity.OrganizerDetails;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.OrganizerDetailsRepository;
import com.trekconnect.core.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing Organizer onboarding applications and status checks.
 */
@Service
public class OrganizerService {

    private final OrganizerDetailsRepository organizerDetailsRepository;
    private final UserProfileRepository userProfileRepository;

    @Autowired
    public OrganizerService(OrganizerDetailsRepository organizerDetailsRepository, UserProfileRepository userProfileRepository) {
        this.organizerDetailsRepository = organizerDetailsRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public OrganizerApplicationResponse applyForOrganizer(String userId, OrganizerApplicationRequest request) {
        UserProfile userProfile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    UserProfile newProfile = UserProfile.builder().userId(userId).name("Trekker").role("USER").build();
                    return userProfileRepository.save(newProfile);
                });

        OrganizerDetails existing = organizerDetailsRepository.findByUserUserId(userId).orElse(null);

        if (existing != null) {
            existing.setOrganizationName(request.getOrganizationName());
            if (request.getVerificationDocsUrl() != null) {
                existing.setVerificationDocsUrl(request.getVerificationDocsUrl());
            }
            existing.setVerificationStatus("PENDING");
            existing.setRejectionReason(null);
            OrganizerDetails saved = organizerDetailsRepository.save(existing);
            return mapToResponse(saved);
        }

        OrganizerDetails application = OrganizerDetails.builder()
                .user(userProfile)
                .organizationName(request.getOrganizationName())
                .verificationDocsUrl(request.getVerificationDocsUrl())
                .verificationStatus("PENDING")
                .build();

        OrganizerDetails saved = organizerDetailsRepository.save(application);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public OrganizerApplicationResponse getApplicationStatus(String userId) {
        OrganizerDetails application = organizerDetailsRepository.findByUserUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("No organizer application found for current user."));

        return mapToResponse(application);
    }

    private OrganizerApplicationResponse mapToResponse(OrganizerDetails details) {
        return OrganizerApplicationResponse.builder()
                .id(details.getId())
                .userId(details.getUser().getUserId())
                .organizationName(details.getOrganizationName())
                .verificationStatus(details.getVerificationStatus())
                .verificationDocsUrl(details.getVerificationDocsUrl())
                .rejectionReason(details.getRejectionReason())
                .verifiedAt(details.getVerifiedAt())
                .build();
    }
}
