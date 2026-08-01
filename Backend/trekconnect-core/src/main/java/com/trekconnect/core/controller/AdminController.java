package com.trekconnect.core.controller;

import com.trekconnect.core.dto.request.RejectOrganizerRequest;
import com.trekconnect.core.dto.response.AdminDashboardStatsResponse;
import com.trekconnect.core.dto.response.DisputeResponse;
import com.trekconnect.core.dto.response.OrganizerApplicationResponse;
import com.trekconnect.core.service.AdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller exposing Admin verification & dispute oversight endpoints.
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/organizers")
    public ResponseEntity<List<OrganizerApplicationResponse>> getAllOrganizerApplications(
            @RequestParam(value = "status", required = false) String status) {
        List<OrganizerApplicationResponse> response = adminService.getAllOrganizerApplications(status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/organizers/{id}/verify")
    public ResponseEntity<OrganizerApplicationResponse> verifyOrganizer(
            Authentication authentication,
            @PathVariable("id") String id) {
        String adminId = (String) authentication.getPrincipal();
        OrganizerApplicationResponse response = adminService.verifyOrganizer(adminId, id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/organizers/{id}/reject")
    public ResponseEntity<OrganizerApplicationResponse> rejectOrganizer(
            Authentication authentication,
            @PathVariable("id") String id,
            @Valid @RequestBody RejectOrganizerRequest request) {
        String adminId = (String) authentication.getPrincipal();
        OrganizerApplicationResponse response = adminService.rejectOrganizer(adminId, id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/disputes")
    public ResponseEntity<List<DisputeResponse>> getAllDisputes(
            @RequestParam(value = "status", required = false) String status) {
        List<DisputeResponse> response = adminService.getAllDisputes(status);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/disputes/{id}/resolve")
    public ResponseEntity<DisputeResponse> resolveDispute(
            Authentication authentication,
            @PathVariable("id") String id,
            @RequestParam(value = "resolution", defaultValue = "APPROVED") String resolution) {
        String adminId = (String) authentication.getPrincipal();
        DisputeResponse response = adminService.resolveDispute(adminId, id, resolution);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/stats")
    public ResponseEntity<AdminDashboardStatsResponse> getDashboardStats() {
        AdminDashboardStatsResponse response = adminService.getDashboardStats();
        return ResponseEntity.ok(response);
    }
}
