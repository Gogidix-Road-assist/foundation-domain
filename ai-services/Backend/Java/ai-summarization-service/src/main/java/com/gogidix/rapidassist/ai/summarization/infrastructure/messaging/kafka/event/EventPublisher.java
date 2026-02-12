package com.gogidix.rapidassist.ai.summarization.infrastructure.messaging.kafka.event;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Object event) {
        try {
            String topic = getTopicForEvent(event);
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topic, event.getClass().getSimpleName(), payload);
            log.info("Published event: {} to topic: {}", event.getClass().getSimpleName(), topic);
        } catch (Exception e) {
            log.error("Failed to publish event: {}", event.getClass().getSimpleName(), e);
        }
    }

    private String getTopicForEvent(Object event) {
        if (event.getClass().getSimpleName().contains("SummarizationRequestCreated")) {
            return "summarization.request.created";
        } else if (event.getClass().getSimpleName().contains("SummarizationCompleted")) {
            return "summarization.completed";
        } else if (event.getClass().getSimpleName().contains("SummarizationFailed")) {
            return "summarization.failed";
        }
        return "summarization.events";
    }
}
