package com.trekconnect.auth.security;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;

/**
 * Key Provider managing RSA 2048-bit Asymmetric Key Pairs for RS256 JWT signing.
 * 
 * WHY THIS CLASS WAS CREATED:
 * Implements asymmetric RS256 signing architecture. The Auth Service owns the RSA Private Key
 * used to sign JWT access tokens, and exposes the RSA Public Key via JWKS endpoint so the Monolith
 * and API Gateways can verify tokens locally without shared secret distribution.
 */
@Component
public class JwtKeyProvider {

    private static final Logger log = LoggerFactory.getLogger(JwtKeyProvider.class);

    @Value("${jwt.key-id:trekconnect-auth-rs256-key-1}")
    private String keyId;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public JwtKeyProvider() {}

    @PostConstruct
    public void init() {
        log.info("Initializing RSA 2048-bit Key Pair for RS256 JWT signing...");
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            this.privateKey = (RSAPrivateKey) keyPair.getPrivate();
            this.publicKey = (RSAPublicKey) keyPair.getPublic();

            log.info("RSA Key Pair successfully generated with KeyId: {}", keyId);
        } catch (Exception e) {
            log.error("Failed to generate RSA 2048 key pair: {}", e.getMessage(), e);
            throw new IllegalStateException("Could not initialize RSA key pair for JWT signing", e);
        }
    }

    public String getJwksJson() {
        String modulus = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getModulus().toByteArray());
        String exponent = Base64.getUrlEncoder().withoutPadding().encodeToString(publicKey.getPublicExponent().toByteArray());

        return String.format(
                "{\"keys\":[{\"kty\":\"RSA\",\"use\":\"sig\",\"alg\":\"RS256\",\"kid\":\"%s\",\"n\":\"%s\",\"e\":\"%s\"}]}",
                keyId, modulus, exponent
        );
    }

    public String getKeyId() {
        return keyId;
    }

    public RSAPrivateKey getPrivateKey() {
        return privateKey;
    }

    public RSAPublicKey getPublicKey() {
        return publicKey;
    }
}
