package com.gogidix.rapidassist.crossdomain.vendors.application;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Data;

import com.gogidix.rapidassist.crossdomain.vendors.domain.model.PartsOrderRequest;
import com.gogidix.rapidassist.crossdomain.vendors.domain.model.VendorPartsCatalog;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MechanicsVendorsBridgeService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.mechanics.inventory-service}")
    private String mechanicsInventoryService;

    @Value("${services.mechanics.ordering-service}")
    private String mechanicsOrderingService;

    @Value("${services.vendors.base-url}")
    private String vendorsBaseService;

    @Value("${auto-reorder.low-stock-threshold:10}")
    private Integer lowStockThreshold;

    @Value("${auto-reorder.default-reorder-quantity:20}")
    private Integer defaultReorderQuantity;

    /**
     * Get vendor catalog with available parts and pricing
     */
    @CircuitBreaker(name = "vendorsService", fallbackMethod = "getVendorCatalogFallback")
    @Retry(name = "vendorsService")
    public List<VendorPartsCatalog> getVendorCatalog() {
        log.info("Fetching vendor parts catalog...");

        return webClientBuilder.build()
            .get()
            .uri(vendorsBaseService + "/api/vendors/catalog")
            .retrieve()
            .bodyToFlux(VendorPartsCatalog.class)
            .collectList()
            .block();
    }

    /**
     * Search for part across all vendors
     */
    @CircuitBreaker(name = "vendorsService", fallbackMethod = "searchPartsFallback")
    @Retry(name = "vendorsService")
    public List<VendorPartsCatalog.PriceComparison> searchPartAcrossVendors(String partNumber) {
        log.info("Searching for part {} across all vendors", partNumber);

        return webClientBuilder.build()
            .get()
            .uri(vendorsBaseService + "/api/vendors/parts/search?partNumber=" + partNumber)
            .retrieve()
            .bodyToFlux(VendorPartsCatalog.PriceComparison.class)
            .collectList()
            .block();
    }

    /**
     * Submit order to vendor
     */
    @CircuitBreaker(name = "vendorsService", fallbackMethod = "submitOrderFallback")
    @Retry(name = "vendorsService")
    public PartsOrderRequest submitOrderToVendor(PartsOrderRequest order) {
        log.info("Submitting order {} to vendor {}", order.getOrderId(), order.getVendorId());

        return webClientBuilder.build()
            .post()
            .uri(vendorsBaseService + "/api/vendors/" + order.getVendorId() + "/orders")
            .bodyValue(order)
            .retrieve()
            .bodyToMono(PartsOrderRequest.class)
            .block();
    }

    /**
     * Get order status from vendor
     */
    @CircuitBreaker(name = "vendorsService", fallbackMethod = "getOrderStatusFallback")
    @Retry(name = "vendorsService")
    public PartsOrderRequest.OrderStatus getOrderStatus(String orderId, String vendorId) {
        log.info("Checking order {} status from vendor {}", orderId, vendorId);

        return webClientBuilder.build()
            .get()
            .uri(vendorsBaseService + "/api/vendors/" + vendorId + "/orders/" + orderId + "/status")
            .retrieve()
            .bodyToMono(PartsOrderRequest.OrderStatus.class)
            .block();
    }

    /**
     * Auto-reorder low stock parts
     * Scheduled to run every 5 minutes
     */
    @Scheduled(fixedRateString = "${auto-reorder.check-interval:300000}")
    public void autoReorderLowStock() {
        log.info("Checking for low stock parts to auto-reorder...");

        try {
            // Get low stock alerts from inventory service
            List<LowStockAlert> lowStockAlerts = getLowStockAlerts();

            if (lowStockAlerts.isEmpty()) {
                log.info("No low stock parts requiring reorder");
                return;
            }

            // Group alerts by workshop
            Map<String, List<LowStockAlert>> byWorkshop = lowStockAlerts.stream()
                .collect(java.util.stream.Collectors.groupingBy(LowStockAlert::getWorkshopId));

            // Process each workshop
            byWorkshop.forEach((workshopId, alerts) -> {
                processWorkshopReorders(workshopId, alerts);
            });

        } catch (Exception e) {
            log.error("Error during auto-reorder: {}", e.getMessage(), e);
        }
    }

    /**
     * Create consolidated order for workshop
     */
    public PartsOrderRequest createConsolidatedOrder(
        String workshopId,
        List<LowStockAlert> alerts,
        String preferredVendorId
    ) {
        // Search for best vendor if not specified
        String vendorId = preferredVendorId;
        if (vendorId == null) {
            vendorId = findBestVendor(alerts);
        }

        // Build order items
        List<PartsOrderRequest.OrderItem> items = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (LowStockAlert alert : alerts) {
            VendorPartsCatalog.Part part = getPartFromVendor(vendorId, alert.getPartNumber());
            if (part != null) {
                int quantity = calculateReorderQuantity(alert.getCurrentStock(), alert.getMinStock());
                BigDecimal itemTotal = part.getPrice().multiply(BigDecimal.valueOf(quantity));

                items.add(PartsOrderRequest.OrderItem.builder()
                    .partNumber(part.getPartNumber())
                    .partName(part.getPartName())
                    .quantity(quantity)
                    .unitPrice(part.getPrice())
                    .totalPrice(itemTotal)
                    .build());

                totalAmount = totalAmount.add(itemTotal);
            }
        }

        // Get workshop address
        String shippingAddress = getWorkshopAddress(workshopId);

        return PartsOrderRequest.builder()
            .orderId("AUTO-ORD-" + System.currentTimeMillis())
            .workshopId(workshopId)
            .vendorId(vendorId)
            .requestedAt(LocalDateTime.now())
            .items(items)
            .totalAmount(totalAmount)
            .shippingAddress(shippingAddress)
            .status(PartsOrderRequest.OrderStatus.PENDING)
            .priority("MEDIUM")
            .build();
    }

    // ==================== PRIVATE METHODS ====================

    private List<LowStockAlert> getLowStockAlerts() {
        try {
            return webClientBuilder.build()
                .get()
                .uri(mechanicsInventoryService + "/api/inventory/alerts/low-stock?severity=HIGH")
                .retrieve()
                .bodyToFlux(LowStockAlert.class)
                .collectList()
                .block();
        } catch (Exception e) {
            log.error("Failed to get low stock alerts: {}", e.getMessage());
            return List.of();
        }
    }

    private void processWorkshopReorders(String workshopId, List<LowStockAlert> alerts) {
        log.info("Processing {} reorder requests for workshop {}", alerts.size(), workshopId);

        // Find best vendor for these parts
        String vendorId = findBestVendor(alerts);

        // Create consolidated order
        PartsOrderRequest order = createConsolidatedOrder(workshopId, alerts, vendorId);

        // Submit order
        try {
            PartsOrderRequest submittedOrder = submitOrderToVendor(order);
            log.info("Auto-order {} submitted successfully for workshop {}",
                submittedOrder.getOrderId(), workshopId);

            // Update local ordering service
            notifyOrderingService(submittedOrder);

        } catch (Exception e) {
            log.error("Failed to submit auto-order for workshop {}: {}",
                workshopId, e.getMessage(), e);
        }
    }

    private String findBestVendor(List<LowStockAlert> alerts) {
        // For now, return first vendor that has all parts
        // In production, this would compare prices and delivery times
        return "VENDOR-001";
    }

    private VendorPartsCatalog.Part getPartFromVendor(String vendorId, String partNumber) {
        try {
            return webClientBuilder.build()
                .get()
                .uri(vendorsBaseService + "/api/vendors/" + vendorId + "/parts/" + partNumber)
                .retrieve()
                .bodyToMono(VendorPartsCatalog.Part.class)
                .block();
        } catch (Exception e) {
            log.error("Failed to get part {} from vendor {}: {}",
                partNumber, vendorId, e.getMessage());
            return null;
        }
    }

    private int calculateReorderQuantity(int currentStock, int minStock) {
        // Reorder enough to reach minStock + defaultReorderQuantity
        return (minStock + defaultReorderQuantity) - currentStock;
    }

    private String getWorkshopAddress(String workshopId) {
        try {
            return webClientBuilder.build()
                .get()
                .uri(mechanicsOrderingService + "/api/workshops/" + workshopId + "/address")
                .retrieve()
                .bodyToMono(String.class)
                .block();
        } catch (Exception e) {
            log.error("Failed to get workshop address: {}", e.getMessage());
            return "Default Address";
        }
    }

    private void notifyOrderingService(PartsOrderRequest order) {
        try {
            webClientBuilder.build()
                .post()
                .uri(mechanicsOrderingService + "/api/orders/auto-reorder")
                .bodyValue(order)
                .retrieve()
                .bodyToMono(Void.class)
                .block();
        } catch (Exception e) {
            log.error("Failed to notify ordering service: {}", e.getMessage());
        }
    }

    // ==================== FALLBACK METHODS ====================

    private List<VendorPartsCatalog> getVendorCatalogFallback(Exception e) {
        log.error("Fallback: Unable to fetch vendor catalog: {}", e.getMessage());
        return List.of();
    }

    private List<VendorPartsCatalog.PriceComparison> searchPartsFallback(String partNumber, Exception e) {
        log.error("Fallback: Unable to search part {}: {}", partNumber, e.getMessage());
        return List.of();
    }

    private PartsOrderRequest submitOrderFallback(PartsOrderRequest order, Exception e) {
        log.error("Fallback: Unable to submit order to vendor: {}", e.getMessage());
        order.setStatus(PartsOrderRequest.OrderStatus.FAILED);
        return order;
    }

    private PartsOrderRequest.OrderStatus getOrderStatusFallback(String orderId, String vendorId, Exception e) {
        log.error("Fallback: Unable to get order status: {}", e.getMessage());
        return PartsOrderRequest.OrderStatus.FAILED;
    }

    // ==================== DTOs ====================

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class LowStockAlert {
        private String partId;
        private String partNumber;
        private String partName;
        private String workshopId;
        private Integer currentStock;
        private Integer minStock;
        private String severity;
    }
}
