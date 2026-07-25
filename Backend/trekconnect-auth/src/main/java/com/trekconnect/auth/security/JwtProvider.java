package com.trekconnect.auth.security;

import com.trekconnect.auth.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Service component responsible for issuing, signing, and validating RS256 JWT access tokens.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Encapsulates asymmetric RS256 JWT token creation using the RSA Private Key from JwtKeyProvider.
 * Embeds standard JWT claims (sub=userId, email, role, jti, iat, exp).
 */
@Component
public class JwtProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtProvider.class);

    private final JwtKeyProvider jwtKeyProvider;

    @Value("${jwt.access-token-expiration-ms:900000}") // Default: 15 minutes (900,000 ms)
    private long accessTokenExpirationMs;

    @Autowired
    public JwtProvider(JwtKeyProvider jwtKeyProvider) {
        this.jwtKeyProvider = jwtKeyProvider;
    }

    public String generateAccessToken(String userId, String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);
        String jti = UUID.randomUUID().toString();

        log.debug("Generating RS256 JWT Access Token for userId: {}, role: {}", userId, role);

        return Jwts.builder()
                .header()
                .keyId(jwtKeyProvider.getKeyId())
                .and()
                .subject(userId)
                .claim("email", email)
                .claim("role", role.name())
                .id(jti)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(jwtKeyProvider.getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(jwtKeyProvider.getPublicKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT access token: {}", e.getMessage());
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtKeyProvider.getPublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String getJtiFromToken(String token) {
        return getClaimsFromToken(token).getId();
    }

    public Date getExpirationFromToken(String token) {
        return getClaimsFromToken(token).getExpiration();
    }

    public long getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }
}
