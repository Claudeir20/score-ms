package dev.mota.score_ms.service;

import dev.mota.score_ms.event.CreditRequestedEvent;
import dev.mota.score_ms.publisher.CreditDecisionPublisher;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class CreditScoreServiceTest {

    private final CreditDecisionPublisher publisher = Mockito.mock(CreditDecisionPublisher.class);
    private final CreditScoreService scoreService = new CreditScoreService(publisher);


    @Test
    void shouldApproveWhenScoreIsGreaterThanOrEqualTo60(){

        UUID requestId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        String name = "Carlos";
        String email = "carlos@email.com";

        CreditRequestedEvent event = new CreditRequestedEvent(
                UUID.randomUUID(),
                requestId,
                "12345678900",
                name,
                email,
                new BigDecimal("6000.00"),
                new BigDecimal("20000.00"),
                24,
                correlationId,
                LocalDateTime.now()
        );

        scoreService.analyze(event);

        verify(publisher).publishApproved(requestId, name, email, correlationId);
        verify(publisher, never()).publishRejected(requestId, name, email, correlationId);
    }


    @Test
    void shouldRejectWhenScoreIsLowerThan60(){

        UUID requestId = UUID.randomUUID();
        UUID correlationId = UUID.randomUUID();
        String name = "Jose";
        String email = "jose@email.com";

        CreditRequestedEvent event  = new CreditRequestedEvent(
                UUID.randomUUID(),
                requestId,
                "12345678900",
                name,
                email,
                new BigDecimal("2000.00"),
                new BigDecimal("20000.00"),
                48,
                correlationId,
                LocalDateTime.now()

        );

        scoreService.analyze(event);

        verify(publisher).publishRejected(requestId, name, email, correlationId);
        verify(publisher, never()).publishApproved(requestId, name, email, correlationId);
    }
}