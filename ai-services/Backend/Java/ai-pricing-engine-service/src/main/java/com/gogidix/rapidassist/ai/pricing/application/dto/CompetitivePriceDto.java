package com.gogidix.rapidassist.ai.pricing.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for CompetitivePrice.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompetitivePriceDto {

    private UUID id;
    private String tenantId;
    private String productId;
    private String competitorName;
    private String competitorUrl;
    private BigDecimal competitorPrice;
    private BigDecimal ourPrice;
    private BigDecimal priceDifference;
    private BigDecimal percentageDifference;
    private String pricePosition;
    private boolean areWeCheaper;
    private String competitorProductName;
    private boolean inStock;
    private boolean dataStale;
    private LocalDateTime lastChecked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
