package com.gogidix.rapidassist.crossdomain.vendors.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

import com.gogidix.rapidassist.shared.persistence.library.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entity for storing parts order records.
 * Tracks orders between Mechanics and Vendors-Ecommerce domains.
 */
@Entity
@Table(name = "parts_orders", indexes = {
    @Index(name = "idx_order_id", columnList = "order_id"),
    @Index(name = "idx_workshop_id", columnList = "workshop_id"),
    @Index(name = "idx_vendor_id", columnList = "vendor_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_requested_at", columnList = "requested_at"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PartsOrderEntity extends BaseEntity {

    @Column(name = "order_id", nullable = false, length = 100, unique = true)
    private String orderId;

    @Column(name = "workshop_id", nullable = false, length = 100)
    private String workshopId;

    @Column(name = "vendor_id", nullable = false, length = 100)
    private String vendorId;

    @Column(name = "vendor_name", length = 255)
    private String vendorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "shipping_address", length = 500)
    private String shippingAddress;

    @Column(name = "priority", length = 20)
    private String priority;

    @Column(name = "order_type", length = 20)
    private String orderType; // AUTO, MANUAL, URGENT

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "expected_delivery")
    private LocalDateTime expectedDelivery;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "internal_reference", length = 100)
    private String internalReference;

    @Column(name = "special_instructions", length = 1000)
    private String specialInstructions;

    @Column(name = "auto_order", nullable = false)
    private Boolean autoOrder;

    @Column(name = "reorder_reason", length = 255)
    private String reorderReason; // LOW_STOCK, EMERGENCY, STOCKOUT

    /**
     * Order status enum
     */
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        FAILED
    }

    /**
     * Check if order is pending confirmation
     */
    public boolean isPending() {
        return status == OrderStatus.PENDING;
    }

    /**
     * Check if order is in transit
     */
    public boolean isInTransit() {
        return status == OrderStatus.PROCESSING || status == OrderStatus.SHIPPED;
    }

    /**
     * Check if order is complete
     */
    public boolean isComplete() {
        return status == OrderStatus.DELIVERED;
    }

    /**
     * Check if order is failed or cancelled
     */
    public boolean isFailed() {
        return status == OrderStatus.FAILED || status == OrderStatus.CANCELLED;
    }
}
