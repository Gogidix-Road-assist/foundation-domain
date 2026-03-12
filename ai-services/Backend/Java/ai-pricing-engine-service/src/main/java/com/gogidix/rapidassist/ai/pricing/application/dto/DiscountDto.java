package com.gogidix.rapidassist.ai.pricing.application.dto;

import com.gogidix.rapidassist.ai.pricing.domain.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO for Discount.
 */
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiscountDto {

    @EqualsAndHashCode.Include


    private UUID id;
    private String tenantId;
    private String code;
    private String name;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal maxDiscountAmount;
    private BigDecimal minPurchaseAmount;
    private Integer usageLimit;
    private Integer usageCount;
    private Integer remainingUsage;
    private String productId;
    private String categoryId;
    private String customerId;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean active;
    private boolean valid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
}
