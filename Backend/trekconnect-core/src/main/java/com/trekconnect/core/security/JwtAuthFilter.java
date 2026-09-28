package com.trekconnect.core.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

/**
 * Spring Security Filter that validates RS256 JWT access tokens on incoming Monolith requests.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Intercepts incoming HTTP requests to /api/**, extracts Bearer access token, verifies signature
 * locally using Auth Service's public key, and sets authenticated principal in SecurityContext.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private final JwtPublicKeyProvider jwtPublicKeyProvider;
    private final ObjectMapper objectMapper;

    public JwtAuthFilter(JwtPublicKeyProvider jwtPublicKeyProvider) {
        this.jwtPublicKeyProvider = jwtPublicKeyProvider;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Step 1: Check if Authorization header is present with Bearer token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                // Step 2: Extract kid from Base64Url-encoded JWT header
                String kid = extractKidFromHeader(token);
                if (kid == null) {
                    kid = "trekconnect-auth-rs256-key-1";
                }

                // Step 3: Retrieve public key from provider cache
                PublicKey publicKey = jwtPublicKeyProvider.getPublicKey(kid);
                if (publicKey != null) {
                    // Step 4: Validate token signature and parse claims using public key
                    Claims claims = Jwts.parser()
                            .verifyWith(publicKey)
                            .build()
                            .parseSignedClaims(token)
                            .getPayload();

                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);

                    if (userId != null && role != null) {
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userId, null, List.of(authority));

                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        log.debug("Authenticated request for userId: {}, role: {}", userId, role);
                    }
                }
            } catch (Exception e) {
                log.warn("JWT authentication failed for request [{}]: {}", request.getRequestURI(), e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractKidFromHeader(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                String headerJson = new String(Base64.getUrlDecoder().decode(parts[0]));
                JsonNode headerNode = objectMapper.readTree(headerJson);
                if (headerNode.has("kid")) {
                    return headerNode.get("kid").asText();
                }
            }
        } catch (Exception e) {
            log.trace("Could not parse kid from token header", e);
        }
        return null;
    }
}
