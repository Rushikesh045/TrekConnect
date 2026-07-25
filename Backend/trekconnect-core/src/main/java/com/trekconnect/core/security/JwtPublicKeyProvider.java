package com.trekconnect.core.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Security Provider that fetches and caches Auth Service's RSA Public Key (JWKS).
 * 
 * WHY THIS CLASS WAS CREATED:
 * Implements local JWT verification without per-request network calls to Auth Service.
 * Fetches public key JWKS from http://localhost:8081/auth/.well-known/jwks.json
 * and caches it in memory.
 */
@Component
@Slf4j
public class JwtPublicKeyProvider {

    @Value("${auth.service.base-url:http://localhost:8081}")
    private String authServiceBaseUrl;

    private final Map<String, PublicKey> keyCache = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Retrieves the RSA Public Key corresponding to the given Key ID (kid).
     * 
     * WHY THIS METHOD WAS CREATED:
     * Returns cached RSA public key or fetches it on-demand from Auth Service's JWKS endpoint.
     */
    public PublicKey getPublicKey(String kid) {
        if (keyCache.containsKey(kid)) {
            return keyCache.get(kid);
        }

        synchronized (this) {
            if (keyCache.containsKey(kid)) {
                return keyCache.get(kid);
            }
            fetchAndCacheKeys();
            return keyCache.get(kid);
        }
    }

    /**
     * Fetches public key JSON Web Key Set (JWKS) from Auth Service.
     */
    private void fetchAndCacheKeys() {
        String jwksUrl = authServiceBaseUrl + "/auth/.well-known/jwks.json";
        log.info("Fetching RSA Public Keys (JWKS) from Auth Service: {}", jwksUrl);
        try {
            String jwksResponse = restTemplate.getForObject(jwksUrl, String.class);
            if (jwksResponse != null) {
                JsonNode root = objectMapper.readTree(jwksResponse);
                JsonNode keys = root.get("keys");
                if (keys != null && keys.isArray()) {
                    for (JsonNode keyNode : keys) {
                        String kid = keyNode.get("kid").asText();
                        String nStr = keyNode.get("n").asText();
                        String eStr = keyNode.get("e").asText();

                        PublicKey publicKey = constructRsaPublicKey(nStr, eStr);
                        keyCache.put(kid, publicKey);
                        log.info("Successfully cached RSA Public Key with KeyId: {}", kid);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Failed to fetch JWKS public key from Auth Service at {}: {}", jwksUrl, e.getMessage());
        }
    }

    private PublicKey constructRsaPublicKey(String nBase64Url, String eBase64Url) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(nBase64Url);
        byte[] eBytes = Base64.getUrlDecoder().decode(eBase64Url);

        BigInteger modulus = new BigInteger(1, nBytes);
        BigInteger publicExponent = new BigInteger(1, eBytes);

        RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, publicExponent);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }
}
