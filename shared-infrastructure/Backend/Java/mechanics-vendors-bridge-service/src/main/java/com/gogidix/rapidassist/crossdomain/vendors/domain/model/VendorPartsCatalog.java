package com.gogidix.rapidassist.crossdomain.vendors.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPartsCatalog {
    private String vendorId;
    private String vendorName;
    private String contactEmail;
    private String contactPhone;
    private boolean approved;
    private BigDecimal rating;
    private List<Part> parts;
    private Integer averageDeliveryTimeDays;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Part {
        private String partNumber;
        private String partName;
        private String category;
        private BigDecimal price;
        private Integer availableQuantity;
        private Integer minimumOrderQuantity;
        private boolean inStock;
        private String description;
        private String brand;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceComparison {
        private String partNumber;
        private String partName;
        private List<VendorPrice> vendorPrices;
        private VendorPrice lowestPrice;
        private VendorPrice fastestDelivery;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class VendorPrice {
            private String vendorId;
            private String vendorName;
            private BigDecimal price;
            private Integer deliveryDays;
            private Integer availableQuantity;
        }
    }
}
