package dev.mota.score_ms.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditApprovedEvent(
        UUID eventId,
        UUID requestId,
        UUID correlationId,
        LocalDateTime occurred
) {}