package com.gogidix.rapidassist.ai.inference.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Domain model representing an inference result
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "inference_results")
public class InferenceResult {

    @Id
    private UUID id;

    @Indexed
    private UUID inferenceRequestId;

    @Indexed
    private String tenantId;

    private String outputData;
    private String modelVersion;
    private Double confidence;
    private Integer latencyMillis;
    private LocalDateTime timestamp;
    private java.util.Map<String, Object> additionalData;
    private String resultType;

    public static InferenceResult create(String tenantId, String outputData,
                                        String modelVersion, Double confidence,
                                        Integer latencyMillis, String resultType) {
        return InferenceResult.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .outputData(outputData)
                .modelVersion(modelVersion)
                .confidence(confidence)
                .latencyMillis(latencyMillis)
                .timestamp(LocalDateTime.now())
                .additionalData(new java.util.HashMap<>())
                .resultType(resultType)
                .build();
    }

    public void addAdditionalData(String key, Object value) {
        if (this.additionalData == null) {
            this.additionalData = new java.util.HashMap<>();
        }
        this.additionalData.put(key, value);
    }
}
