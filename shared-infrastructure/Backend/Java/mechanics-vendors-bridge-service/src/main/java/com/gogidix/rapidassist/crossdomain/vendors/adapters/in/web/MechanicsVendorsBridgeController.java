package com.gogidix.rapidassist.crossdomain.vendors.adapters.in.web;

import com.gogidix.rapidassist.crossdomain.vendors.application.MechanicsVendorsBridgeService;
import com.gogidix.rapidassist.crossdomain.vendors.domain.model.PartsOrderRequest;
import com.gogidix.rapidassist.crossdomain.vendors.domain.model.VendorPartsCatalog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bridge/mechanics-vendors")
@Slf4j
@RequiredArgsConstructor
public class MechanicsVendorsBridgeController {

    private final MechanicsVendorsBridgeService bridgeService;

    // ==================== VENDOR CATALOG ====================

    @GetMapping("/catalog")
    public ResponseEntity<List<VendorPartsCatalog>> getVendorCatalog() {
        log.info("Fetching vendor catalog");

        List<VendorPartsCatalog> catalog = bridgeService.getVendorCatalog();
        return ResponseEntity.ok(catalog);
    }

    @GetMapping("/catalog/search")
    public ResponseEntity<List<VendorPartsCatalog.PriceComparison>> searchPart(
        @RequestParam String partNumber
    ) {
        log.info("Searching for part {} across vendors", partNumber);

        List<VendorPartsCatalog.PriceComparison> results = bridgeService.searchPartAcrossVendors(partNumber);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/catalog/{vendorId}")
    public ResponseEntity<VendorPartsCatalog> getVendorDetails(@PathVariable String vendorId) {
        log.info("Fetching details for vendor {}", vendorId);

        // This would call the service to get specific vendor details
        return ResponseEntity.ok().build();
    }

    // ==================== ORDER MANAGEMENT ====================

    @PostMapping("/orders")
    public ResponseEntity<PartsOrderRequest> submitOrder(@RequestBody PartsOrderRequest order) {
        log.info("Submitting order {} to vendor {}", order.getOrderId(), order.getVendorId());

        PartsOrderRequest submittedOrder = bridgeService.submitOrderToVendor(order);
        return ResponseEntity.ok(submittedOrder);
    }

    @PostMapping("/orders/consolidated")
    public ResponseEntity<PartsOrderRequest> createConsolidatedOrder(@RequestBody Map<String, Object> request) {
        String workshopId = (String) request.get("workshopId");
        String preferredVendorId = (String) request.get("preferredVendorId");

        @SuppressWarnings("unchecked")
        List<MechanicsVendorsBridgeService.LowStockAlert> alerts =
            (List<MechanicsVendorsBridgeService.LowStockAlert>) request.get("alerts");

        log.info("Creating consolidated order for workshop {}", workshopId);

        PartsOrderRequest order = bridgeService.createConsolidatedOrder(
            workshopId, alerts, preferredVendorId
        );

        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/{orderId}/status")
    public ResponseEntity<PartsOrderRequest.OrderStatus> getOrderStatus(
        @PathVariable String orderId,
        @RequestParam String vendorId
    ) {
        log.info("Fetching status for order {} from vendor {}", orderId, vendorId);

        PartsOrderRequest.OrderStatus status = bridgeService.getOrderStatus(orderId, vendorId);
        return ResponseEntity.ok(status);
    }

    // ==================== AUTO-REORDER ====================

    @PostMapping("/auto-reorder/trigger")
    public ResponseEntity<Map<String, String>> triggerAutoReorder() {
        log.info("Manually triggering auto-reorder process");

        bridgeService.autoReorderLowStock();

        return ResponseEntity.ok(Map.of(
            "message", "Auto-reorder process triggered",
            "status", "running"
        ));
    }

    @GetMapping("/auto-reorder/status")
    public ResponseEntity<Map<String, Object>> getAutoReorderStatus() {
        // Return statistics about auto-reorder
        return ResponseEntity.ok(Map.of(
            "enabled", true,
            "lastRun", "2025-12-25T15:00:00",
            "nextRun", "2025-12-25T15:05:00",
            "threshold", 10
        ));
    }

    // ==================== HEALTH CHECK ====================

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "service", "mechanics-vendors-bridge-service",
            "status", "UP",
            "description", "Cross-domain bridge between Mechanics and Vendors-Ecommerce"
        ));
    }
}
