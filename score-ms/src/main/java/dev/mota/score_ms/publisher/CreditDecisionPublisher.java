package dev.mota.score_ms.publisher;

import dev.mota.score_ms.config.RabbitMQConfig;
import dev.mota.score_ms.event.CreditApprovedEvent;
import dev.mota.score_ms.event.CreditRejectedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditDecisionPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishApproved(UUID requestId, String name, String email, UUID correlationId) {
        CreditApprovedEvent event = new CreditApprovedEvent(
                UUID.randomUUID(),
                requestId,
                name,
                email,
                correlationId,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CREDIT_EXCHANGE,
                RabbitMQConfig.CREDIT_APPROVED_ROUTING_KEY,
                event
        );
    }

    public void publishRejected(UUID requestId, String name, String email, UUID correlationId) {
        CreditRejectedEvent event = new CreditRejectedEvent(
                UUID.randomUUID(),
                requestId,
                name,
                email,
                correlationId,
                LocalDateTime.now()
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.CREDIT_EXCHANGE,
                RabbitMQConfig.CREDIT_REJECTED_ROUTING_KEY,
                event
        );
    }
}