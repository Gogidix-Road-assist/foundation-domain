package com.gogidix.rapidassist.ai.summarization.infrastructure.messaging.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SummarizationEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void publishSummarizationCreated(String event) {
        kafkaTemplate.send("summarization.request.created", event);
        log.info("Published summarization created event");
    }

    public void publishSummarizationCompleted(String event) {
        kafkaTemplate.send("summarization.completed", event);
        log.info("Published summarization completed event");
    }

    public void publishSummarizationFailed(String event) {
        kafkaTemplate.send("summarization.failed", event);
        log.info("Published summarization failed event");
    }
}
