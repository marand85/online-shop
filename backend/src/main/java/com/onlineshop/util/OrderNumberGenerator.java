package com.onlineshop.util;

import java.time.Instant;
import java.util.Base64;

import org.springframework.stereotype.Component;

@Component
public class OrderNumberGenerator {
    /**
     * Generates ULID-based order number (26 chars, chronologically sortable,
     * unguessable)
     */

    public String generateOrderNumber() {
        // Simple version ULID-like (in production dedicated ulid-java library could be
        // used)
        long timestamp = Instant.now().toEpochMilli();
        byte[] randomBytes = new byte[10];
        new java.security.SecureRandom().nextBytes(randomBytes);

        byte[] ulidBytes = new byte[16];
        System.arraycopy(longToBytes(timestamp), 0, ulidBytes, 0, 6);
        System.arraycopy(randomBytes, 0, ulidBytes, 6, 10);

        String encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(ulidBytes);
        return "ORD-" + encoded.substring(0, 22).toUpperCase();
    }

    private byte[] longToBytes(long value) {
        byte[] bytes = new byte[6];
        for (int i = 5; i >= 0; i--) {
            bytes[i] = (byte) (value & 0xff);
            value >>= 8;
        }
        return bytes;
    }
}
