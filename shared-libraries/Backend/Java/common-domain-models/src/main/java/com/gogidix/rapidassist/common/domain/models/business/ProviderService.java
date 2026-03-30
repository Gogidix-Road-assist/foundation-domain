package com.gogidix.rapidassist.common.domain.models.business;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ProviderService entity defining services offered by providers.
 */
@Document(collection = "provider_service")
public class ProviderService {

    @Id
        private String id;

            private Provider provider;

        private String serviceType; // TOWING, JUMP_START, TIRE_CHANGE, FUEL_DELIVERY, LOCKOUT

        private String serviceName;

        private String description;

        private BigDecimal basePrice;

        private BigDecimal pricePerKm;

        private BigDecimal minCharge;

        private BigDecimal maxCharge;

        private Boolean isAvailable = true;

        private Integer estimatedResponseTime; // in minutes

        private LocalDateTime createdAt;

    public ProviderService() {
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public BigDecimal getPricePerKm() {
        return pricePerKm;
    }

    public void setPricePerKm(BigDecimal pricePerKm) {
        this.pricePerKm = pricePerKm;
    }

    public BigDecimal getMinCharge() {
        return minCharge;
    }

    public void setMinCharge(BigDecimal minCharge) {
        this.minCharge = minCharge;
    }

    public BigDecimal getMaxCharge() {
        return maxCharge;
    }

    public void setMaxCharge(BigDecimal maxCharge) {
        this.maxCharge = maxCharge;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getEstimatedResponseTime() {
        return estimatedResponseTime;
    }

    public void setEstimatedResponseTime(Integer estimatedResponseTime) {
        this.estimatedResponseTime = estimatedResponseTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProviderService)) return false;
        ProviderService that = (ProviderService) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
