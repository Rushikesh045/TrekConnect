package com.trekconnect.auth.controller;

import com.trekconnect.auth.dto.request.*;
import com.trekconnect.auth.dto.response.AuthResponse;
import com.trekconnect.auth.dto.response.MessageResponse;
import com.trekconnect.auth.dto.response.UserInfoResponse;
import com.trekconnect.auth.security.JwtProvider;
import com.trekconnect.auth.service.AuthService;
import com.trekconnect.auth.service.EmailVerificationService;
import com.trekconnect.auth.service.PasswordResetService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing all /auth API endpoints.
 * 
 * WHY THIS CONTROLLER WAS CREATED:
 * Exposes REST endpoints for user registration, login, refresh token rotation, logout,
 * email verification, password resets, and token identity details (/auth/me).
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final PasswordResetService passwordResetService;
    private final JwtProvider jwtProvider;

    @Autowired
    public AuthController(
            AuthService authService,
            EmailVerificationService emailVerificationService,
            PasswordResetService passwordResetService,
            JwtProvider jwtProvider) {
        this.authService = authService;
        this.emailVerificationService = emailVerificationService;
        this.passwordResetService = passwordResetService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Received POST /auth/register for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Received POST /auth/login for email: {}", request.getEmail());
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        log.info("Received POST /auth/refresh");
        AuthResponse response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(
            @Valid @RequestBody(required = false) LogoutRequest request,
            HttpServletRequest httpServletRequest) {
        log.info("Received POST /auth/logout");
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        MessageResponse response = authService.logout(request != null ? request : new LogoutRequest(), authHeader);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<MessageResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
        log.info("Received POST /auth/verify-email");
        MessageResponse response = emailVerificationService.verifyEmail(request.getToken());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<MessageResponse> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        log.info("Received POST /auth/resend-verification for email: {}", request.getEmail());
        MessageResponse response = emailVerificationService.resendVerificationToken(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        log.info("Received POST /auth/forgot-password for email: {}", request.getEmail());
        MessageResponse response = passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        log.info("Received POST /auth/reset-password");
        MessageResponse response = passwordResetService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> me(HttpServletRequest request) {
        log.info("Received GET /auth/me");
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        if (!jwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtProvider.getUserIdFromToken(token);
        UserInfoResponse userInfo = authService.getMe(userId);
        return ResponseEntity.ok(userInfo);
    }

    @PostMapping("/admin/create-user")
    public ResponseEntity<com.trekconnect.auth.dto.response.AdminUserCreateResponse> createAdminUser(
            @Valid @RequestBody com.trekconnect.auth.dto.request.AdminUserCreateRequest request) {
        log.info("Received POST /auth/admin/create-user for email: {}", request.getEmail());
        com.trekconnect.auth.dto.response.AdminUserCreateResponse response = authService.createAdminUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<MessageResponse> changePassword(
            @Valid @RequestBody com.trekconnect.auth.dto.request.ChangePasswordRequest request,
            HttpServletRequest httpServletRequest) {
        log.info("Received POST /auth/change-password");
        String authHeader = httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        if (!jwtProvider.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtProvider.getUserIdFromToken(token);
        MessageResponse response = authService.changePassword(userId, request);
        return ResponseEntity.ok(response);
    }
}
