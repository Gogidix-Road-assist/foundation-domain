package com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.pricing.domain.model.DiscountType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * MongoDB Document for Discount.
 * Maps to discount collection with multi-tenancy support.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "discount")
public class DiscountEntity {

    @Id
    private String id;

    @Indexed(unique = true)
    private UUID uuid;

    @Indexed
    private String tenantId;

    @Indexed(unique = true)
    private String code;

    @Indexed
    private String name;

    private String description;

    @Indexed
    private DiscountType discountType;

    private BigDecimal discountValue;

    private BigDecimal maxDiscountAmount;

    private BigDecimal minPurchaseAmount;

    private Integer usageLimit;

    @Indexed
    private Integer usageCount;

    @Indexed
    private String productId;

    @Indexed
    private String categoryId;

    @Indexed
    private String customerId;

    @Indexed
    private LocalDateTime validFrom;

    @Indexed
    private LocalDateTime validUntil;

    @Indexed
    private boolean active;

    @Indexed
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Long version;
}
