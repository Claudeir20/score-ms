package dev.mota.score_ms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String CREDIT_EXCHANGE = "credit.exchange";

    public static final String SCORE_CREDIT_REQUESTED_QUEUE = "score.credit.requested.queue";

    public static final String CREDIT_REQUESTED_ROUTING_KEY = "credit.requested";
    public static final String CREDIT_APPROVED_ROUTING_KEY = "credit.approved";
    public static final String CREDIT_REJECTED_ROUTING_KEY = "credit.rejected";

    public static final String SCORE_DLX = "score.dlx";
    public static final String SCORE_DLQ = "score.dlq";
    public static final String SCORE_DLQ_ROUTING_KEY = "score.dlq";

    @Bean
    public TopicExchange creditExchange() {
        return new TopicExchange(CREDIT_EXCHANGE);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(SCORE_DLX);
    }

    @Bean
    public Queue scoreDlq() {
        return QueueBuilder
                .durable(SCORE_DLQ)
                .build();
    }

    @Bean
    public Binding scoreDlqBinding(
            Queue scoreDlq,
            DirectExchange deadLetterExchange
    ) {
        return BindingBuilder
                .bind(scoreDlq)
                .to(deadLetterExchange)
                .with(SCORE_DLQ_ROUTING_KEY);
    }

    @Bean
    public Queue scoreCreditRequestedQueue() {
        return QueueBuilder
                .durable(SCORE_CREDIT_REQUESTED_QUEUE)
                .deadLetterExchange(SCORE_DLX)
                .deadLetterRoutingKey(SCORE_DLQ_ROUTING_KEY)
                .build();
    }

    @Bean
    public Binding scoreCreditRequestedBinding(
            Queue scoreCreditRequestedQueue,
            TopicExchange creditExchange
    ) {
        return BindingBuilder
                .bind(scoreCreditRequestedQueue)
                .to(creditExchange)
                .with(CREDIT_REQUESTED_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}