package com.trekconnect.auth;

import com.trekconnect.auth.util.TokenHashUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenHashUtilTest {

    @Test
    void testHashToken_Success() {
        String token = "sample-secret-token-123";
        String hash1 = TokenHashUtil.hashToken(token);
        String hash2 = TokenHashUtil.hashToken(token);

        assertNotNull(hash1);
        assertEquals(64, hash1.length()); // SHA-256 hex string is 64 chars
        assertEquals(hash1, hash2);
    }

    @Test
    void testHashToken_Null() {
        assertNull(TokenHashUtil.hashToken(null));
    }
}
