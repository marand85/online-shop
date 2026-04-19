package com.onlineshop.event;

import java.time.Instant;

public record OrderPlacedEvent(
                String orderNumber,
                String contactEmail,
                Integer totalCents,
                String currency,
                Instant palcedAt) {
}
