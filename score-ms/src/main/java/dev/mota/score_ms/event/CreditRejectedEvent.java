package dev.mota.score_ms.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditRejectedEvent(
        UUID eventId,
        UUID requestId,
        UUID correlationId,
        LocalDateTime occurred
) {}