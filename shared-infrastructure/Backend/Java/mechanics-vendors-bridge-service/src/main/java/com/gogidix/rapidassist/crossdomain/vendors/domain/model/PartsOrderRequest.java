package com.gogidix.rapidassist.crossdomain.vendors.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartsOrderRequest {
    private String orderId;
    private String workshopId;
    private String vendorId;
    private LocalDateTime requestedAt;
    private String requestedDeliveryDate;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    private String shippingAddress;
    private OrderStatus status;
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem {
        private String partNumber;
        private String partName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }

    public enum OrderStatus {
        PENDING,
        VENDOR_CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        FAILED
    }
}
