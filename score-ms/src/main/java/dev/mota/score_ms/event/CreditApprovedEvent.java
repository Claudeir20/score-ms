package dev.mota.score_ms.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreditApprovedEvent(
        UUID eventId,
        UUID requestId,
        String name,
        String email,
        UUID correlationId,
        LocalDateTime occurred
) {}