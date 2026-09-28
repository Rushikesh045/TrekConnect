package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.UpdateProfileRequest;
import com.trekconnect.core.dto.response.UserProfileResponse;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service managing User Profile retrieval and updates.
 */
@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Autowired
    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfileResponse getProfile(String userId) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> {
                    // Fallback create default if missing
                    UserProfile defaultProfile = UserProfile.builder()
                            .userId(userId)
                            .name("Trekker Explorer")
                            .role("USER")
                            .build();
                    return userProfileRepository.save(defaultProfile);
                });

        return mapToResponse(profile);
    }

    @Transactional
    public UserProfileResponse updateProfile(String userId, UpdateProfileRequest request) {
        UserProfile profile = userProfileRepository.findById(userId)
                .orElseGet(() -> UserProfile.builder().userId(userId).role("USER").build());

        if (request.getName() != null && !request.getName().isBlank()) {
            profile.setName(request.getName());
        }
        if (request.getPhone() != null) {
            profile.setPhone(request.getPhone());
        }
        if (request.getProfilePicUrl() != null) {
            profile.setProfilePicUrl(request.getProfilePicUrl());
        }
        if (request.getBio() != null) {
            profile.setBio(request.getBio());
        }

        UserProfile saved = userProfileRepository.save(profile);
        return mapToResponse(saved);
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {
        return UserProfileResponse.builder()
                .userId(profile.getUserId())
                .name(profile.getName())
                .phone(profile.getPhone())
                .profilePicUrl(profile.getProfilePicUrl())
                .bio(profile.getBio())
                .role(profile.getRole())
                .createdAt(profile.getCreatedAt())
                .build();
    }
}
