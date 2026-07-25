package com.trekconnect.auth.service;

import com.trekconnect.auth.dto.request.RegisterRequest;
import com.trekconnect.auth.dto.response.AuthResponse;
import com.trekconnect.auth.entity.Role;
import com.trekconnect.auth.entity.UserCredentials;
import com.trekconnect.auth.event.UserRegisteredEventPublisher;
import com.trekconnect.auth.exception.UserAlreadyExistsException;
import com.trekconnect.auth.repository.RefreshTokenRepository;
import com.trekconnect.auth.repository.UserCredentialsRepository;
import com.trekconnect.auth.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialsRepository userCredentialsRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private UserRegisteredEventPublisher userRegisteredEventPublisher;

    @Mock
    private EmailVerificationService emailVerificationService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private UserCredentials savedUser;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .email("test@trekconnect.com")
                .password("Password123!")
                .name("Test Trekker")
                .role(Role.USER)
                .build();

        savedUser = UserCredentials.builder()
                .id("test-uuid-123")
                .email("test@trekconnect.com")
                .passwordHash("hashed-password")
                .role(Role.USER)
                .isEmailVerified(false)
                .isActive(true)
                .failedLoginAttempts(0)
                .build();
    }

    @Test
    void testRegister_Success() {
        when(userCredentialsRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashed-password");
        when(userCredentialsRepository.save(any(UserCredentials.class))).thenReturn(savedUser);
        when(jwtProvider.generateAccessToken(anyString(), anyString(), any(Role.class))).thenReturn("mock-access-token");
        when(jwtProvider.getAccessTokenExpirationMs()).thenReturn(900000L);

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("test@trekconnect.com", response.getUser().getEmail());

        verify(emailVerificationService, times(1)).createVerificationToken(eq("test-uuid-123"));
        verify(userRegisteredEventPublisher, times(1)).publishUserRegistered(any());
    }

    @Test
    void testRegister_UserAlreadyExists_ThrowsException() {
        when(userCredentialsRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.register(registerRequest));

        verify(userCredentialsRepository, never()).save(any());
    }
}
