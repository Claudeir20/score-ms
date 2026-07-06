package dev.mota.score_ms.service;

import dev.mota.score_ms.event.CreditRequestedEvent;
import dev.mota.score_ms.publisher.CreditDecisionPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CreditScoreService {

    private final CreditDecisionPublisher publisher;

    public void analyze(CreditRequestedEvent event) {
        int score = calculateScore(event);

        if (score >= 60) {
            publisher.publishApproved(event.requestId(), event.correlationId());
            return;
        }

        publisher.publishRejected(event.requestId(), event.correlationId());
    }

    private int calculateScore(CreditRequestedEvent event) {
        int score = 0;

        if (event.income().compareTo(new BigDecimal("5000.00")) >= 0) {
            score += 40;
        }

        if (event.valueRequest().compareTo(event.income().multiply(new BigDecimal("4"))) <= 0) {
            score += 25;
        }

        if (event.termMonths() <= 36) {
            score += 20;
        }

        if (event.valueRequest().compareTo(event.income().multiply(new BigDecimal("8"))) <= 0) {
            score += 15;
        }

        return score;
    }
}