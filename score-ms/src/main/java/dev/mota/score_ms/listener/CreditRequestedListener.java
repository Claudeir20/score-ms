package dev.mota.score_ms.listener;

import dev.mota.score_ms.config.RabbitMQConfig;
import dev.mota.score_ms.event.CreditRequestedEvent;
import dev.mota.score_ms.service.CreditScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CreditRequestedListener {

    private final CreditScoreService service;

    @RabbitListener(queues = RabbitMQConfig.SCORE_CREDIT_REQUESTED_QUEUE)
    public void handle(CreditRequestedEvent event) {
        service.analyze(event);
    }
}