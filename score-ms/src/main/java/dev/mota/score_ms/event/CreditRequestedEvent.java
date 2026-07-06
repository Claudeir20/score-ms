package dev.mota.score_ms.event;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreditRequestedEvent(
        UUID eventId,
        UUID requestId,
        String cpf,
        String name,
        BigDecimal income,
        BigDecimal valueRequest,
        Integer termMonths,
        UUID correlationId,
        LocalDateTime occurredAt
) {}