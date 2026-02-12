package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.ai.pricing.domain.model.PriceRule;
import com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity.PriceRuleEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Mapper for converting between PriceRule domain model and PriceRuleEntity.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PriceRulePersistenceMapper {

    private final ObjectMapper objectMapper;

    public PriceRuleEntity toEntity(PriceRule domain) {
        if (domain == null) {
            return null;
        }

        String parametersJson = null;
        if (domain.getParameters() != null) {
            try {
                parametersJson = objectMapper.writeValueAsString(domain.getParameters());
            } catch (JsonProcessingException e) {
                log.error("Error serializing parameters", e);
            }
        }

        return PriceRuleEntity.builder()
                .uuid(domain.getId())
                .tenantId(domain.getTenantId())
                .name(domain.getName())
                .description(domain.getDescription())
                .strategyType(domain.getStrategyType())
                .status(domain.getStatus())
                .basePrice(domain.getBasePrice())
                .minPrice(domain.getMinPrice())
                .maxPrice(domain.getMaxPrice())
                .currentPrice(domain.getCurrentPrice())
                .productId(domain.getProductId())
                .categoryId(domain.getCategoryId())
                .parameters(parametersJson)
                .priority(domain.getPriority())
                .validFrom(domain.getValidFrom())
                .validUntil(domain.getValidUntil())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .createdBy(domain.getCreatedBy())
                .updatedBy(domain.getUpdatedBy())
                .version(1L)
                .build();
    }

    public PriceRule toDomain(PriceRuleEntity entity) {
        if (entity == null) {
            return null;
        }

        Map<String, Object> parameters = null;
        if (entity.getParameters() != null) {
            try {
                parameters = objectMapper.readValue(entity.getParameters(), new TypeReference<Map<String, Object>>() {});
            } catch (JsonProcessingException e) {
                log.error("Error deserializing parameters", e);
            }
        }

        return PriceRule.builder()
                .id(entity.getUuid())
                .tenantId(entity.getTenantId())
                .name(entity.getName())
                .description(entity.getDescription())
                .strategyType(entity.getStrategyType())
                .status(entity.getStatus())
                .basePrice(entity.getBasePrice())
                .minPrice(entity.getMinPrice())
                .maxPrice(entity.getMaxPrice())
                .currentPrice(entity.getCurrentPrice())
                .productId(entity.getProductId())
                .categoryId(entity.getCategoryId())
                .parameters(parameters)
                .priority(entity.getPriority())
                .validFrom(entity.getValidFrom())
                .validUntil(entity.getValidUntil())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
