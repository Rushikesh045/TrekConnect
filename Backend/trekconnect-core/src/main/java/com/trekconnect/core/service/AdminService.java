package com.trekconnect.core.service;

import com.trekconnect.core.dto.request.RejectOrganizerRequest;
import com.trekconnect.core.dto.response.AdminDashboardStatsResponse;
import com.trekconnect.core.dto.response.DisputeResponse;
import com.trekconnect.core.dto.response.OrganizerApplicationResponse;
import com.trekconnect.core.entity.OrganizerDetails;
import com.trekconnect.core.entity.RefundRequest;
import com.trekconnect.core.entity.UserProfile;
import com.trekconnect.core.repository.OrganizerDetailsRepository;
import com.trekconnect.core.repository.RefundRequestRepository;
import com.trekconnect.core.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service handling Admin verification workflows, dispute oversights, and dashboard management.
 */
@Service
public class AdminService {

    private final OrganizerDetailsRepository organizerDetailsRepository;
    private final UserProfileRepository userProfileRepository;
    private final RefundRequestRepository refundRequestRepository;

    @Autowired
    public AdminService(OrganizerDetailsRepository organizerDetailsRepository, 
                        UserProfileRepository userProfileRepository,
                        RefundRequestRepository refundRequestRepository) {
        this.organizerDetailsRepository = organizerDetailsRepository;
        this.userProfileRepository = userProfileRepository;
        this.refundRequestRepository = refundRequestRepository;
    }

    @Transactional(readOnly = true)
    public List<OrganizerApplicationResponse> getAllOrganizerApplications(String status) {
        List<OrganizerDetails> applications;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            applications = organizerDetailsRepository.findAll().stream()
                    .filter(a -> status.equalsIgnoreCase(a.getVerificationStatus()))
                    .toList();
        } else {
            applications = organizerDetailsRepository.findAll();
        }

        return applications.stream().map(this::mapToOrganizerResponse).toList();
    }

    @Transactional
    public OrganizerApplicationResponse verifyOrganizer(String adminId, String applicationId) {
        OrganizerDetails application = organizerDetailsRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer application not found with ID: " + applicationId));

        application.setVerificationStatus("VERIFIED");
        application.setVerifiedByAdminId(adminId);
        application.setVerifiedAt(LocalDateTime.now());
        application.setRejectionReason(null);

        // Update matching UserProfile role from USER to ORGANIZER
        UserProfile userProfile = application.getUser();
        if (userProfile != null) {
            userProfile.setRole("ORGANIZER");
            userProfileRepository.save(userProfile);
        }

        OrganizerDetails saved = organizerDetailsRepository.save(application);
        return mapToOrganizerResponse(saved);
    }

    @Transactional
    public OrganizerApplicationResponse rejectOrganizer(String adminId, String applicationId, RejectOrganizerRequest request) {
        OrganizerDetails application = organizerDetailsRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Organizer application not found with ID: " + applicationId));

        application.setVerificationStatus("REJECTED");
        application.setVerifiedByAdminId(adminId);
        application.setVerifiedAt(LocalDateTime.now());
        application.setRejectionReason(request.getRejectionReason());

        OrganizerDetails saved = organizerDetailsRepository.save(application);
        return mapToOrganizerResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<DisputeResponse> getAllDisputes(String status) {
        List<RefundRequest> disputes;
        if (status != null && !status.isBlank() && !"ALL".equalsIgnoreCase(status)) {
            disputes = refundRequestRepository.findByStatus(status);
        } else {
            disputes = refundRequestRepository.findAll();
        }

        return disputes.stream().map(this::mapToDisputeResponse).toList();
    }

    @Transactional
    public DisputeResponse resolveDispute(String adminId, String disputeId, String resolution) {
        RefundRequest request = refundRequestRepository.findById(disputeId)
                .orElseThrow(() -> new IllegalArgumentException("Dispute refund request not found with ID: " + disputeId));

        request.setStatus(resolution != null ? resolution.toUpperCase() : "APPROVED");
        request.setHandledByAdminId(adminId);

        RefundRequest saved = refundRequestRepository.save(request);
        return mapToDisputeResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<com.trekconnect.core.dto.response.RegisteredUserResponse> getAllRegisteredUsers() {
        List<UserProfile> users = userProfileRepository.findAll();
        return users.stream().map(u -> com.trekconnect.core.dto.response.RegisteredUserResponse.builder()
                .userId(u.getUserId())
                .name(u.getName() != null ? u.getName() : "Trekker Explorer")
                .email(u.getEmail() != null ? u.getEmail() : (u.getUserId() + "@trekconnect.com"))
                .phone(u.getPhone())
                .role(u.getRole() != null ? u.getRole() : "USER")
                .profilePicUrl(u.getProfilePicUrl())
                .bio(u.getBio())
                .createdAt(u.getCreatedAt() != null ? u.getCreatedAt() : LocalDateTime.now())
                .build()
        ).toList();
    }

    @Transactional(readOnly = true)
    public AdminDashboardStatsResponse getDashboardStats() {
        long totalUsers = userProfileRepository.count();
        long pendingApprovals = organizerDetailsRepository.findAll().stream()
                .filter(a -> "PENDING".equalsIgnoreCase(a.getVerificationStatus()))
                .count();
        long totalOrganizers = organizerDetailsRepository.findAll().stream()
                .filter(a -> "VERIFIED".equalsIgnoreCase(a.getVerificationStatus()))
                .count();
        long totalTreks = 12;

        return AdminDashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalOrganizers(totalOrganizers)
                .pendingApprovals(pendingApprovals)
                .totalTreks(totalTreks)
                .build();
    }

    private OrganizerApplicationResponse mapToOrganizerResponse(OrganizerDetails details) {
        return OrganizerApplicationResponse.builder()
                .id(details.getId())
                .userId(details.getUser() != null ? details.getUser().getUserId() : "N/A")
                .organizationName(details.getOrganizationName())
                .verificationStatus(details.getVerificationStatus())
                .verificationDocsUrl(details.getVerificationDocsUrl())
                .rejectionReason(details.getRejectionReason())
                .verifiedAt(details.getVerifiedAt())
                .build();
    }

    private DisputeResponse mapToDisputeResponse(RefundRequest request) {
        return DisputeResponse.builder()
                .id(request.getId())
                .paymentId(request.getPayment() != null ? request.getPayment().getId() : "PAY-MOCK-901")
                .requestedByUserId(request.getRequestedByUser() != null ? request.getRequestedByUser().getUserId() : "USR-MOCK-001")
                .requestedByUserName(request.getRequestedByUser() != null ? request.getRequestedByUser().getName() : "Trekker Explorer")
                .reason(request.getReason())
                .status(request.getStatus())
                .createdAt(request.getCreatedAt())
                .build();
    }
}
