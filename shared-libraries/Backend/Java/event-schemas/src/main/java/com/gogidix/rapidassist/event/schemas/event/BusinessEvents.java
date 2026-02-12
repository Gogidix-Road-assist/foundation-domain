package com.gogidix.rapidassist.event.schemas.event;

/**
 * Business domain events for core operations.
 */
public class BusinessEvents {

    /**
     * Event fired when a service request is created
     */
    public static class ServiceRequestCreated extends DomainEvent {
        private String requestNumber;
        private String customerId;
        private String customerName;
        private String serviceType;
        private String vehicleInfo;
        private String location;

        public ServiceRequestCreated() {
            super();
            this.eventType = "ServiceRequestCreated";
            this.aggregateType = "ServiceRequest";
        }

        public String getRequestNumber() {
            return requestNumber;
        }

        public void setRequestNumber(String requestNumber) {
            this.requestNumber = requestNumber;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getVehicleInfo() {
            return vehicleInfo;
        }

        public void setVehicleInfo(String vehicleInfo) {
            this.vehicleInfo = vehicleInfo;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }
    }

    /**
     * Event fired when a service request is updated
     */
    public static class ServiceRequestUpdated extends DomainEvent {
        private String requestNumber;
        private String updateType;
        private String oldValue;
        private String newValue;

        public ServiceRequestUpdated() {
            super();
            this.eventType = "ServiceRequestUpdated";
            this.aggregateType = "ServiceRequest";
        }

        public String getRequestNumber() {
            return requestNumber;
        }

        public void setRequestNumber(String requestNumber) {
            this.requestNumber = requestNumber;
        }

        public String getUpdateType() {
            return updateType;
        }

        public void setUpdateType(String updateType) {
            this.updateType = updateType;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }
    }

    /**
     * Event fired when a service request is completed
     */
    public static class ServiceRequestCompleted extends DomainEvent {
        private String requestNumber;
        private String customerId;
        private String providerId;
        private String providerName;
        private Integer durationMinutes;
        private Double finalCost;

        public ServiceRequestCompleted() {
            super();
            this.eventType = "ServiceRequestCompleted";
            this.aggregateType = "ServiceRequest";
        }

        public String getRequestNumber() {
            return requestNumber;
        }

        public void setRequestNumber(String requestNumber) {
            this.requestNumber = requestNumber;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }

        public String getProviderName() {
            return providerName;
        }

        public void setProviderName(String providerName) {
            this.providerName = providerName;
        }

        public Integer getDurationMinutes() {
            return durationMinutes;
        }

        public void setDurationMinutes(Integer durationMinutes) {
            this.durationMinutes = durationMinutes;
        }

        public Double getFinalCost() {
            return finalCost;
        }

        public void setFinalCost(Double finalCost) {
            this.finalCost = finalCost;
        }
    }

    /**
     * Event fired when a payment is processed
     */
    public static class PaymentProcessed extends DomainEvent {
        private String paymentReference;
        private String customerId;
        private String paymentMethod;
        private Double amount;
        private String currency;
        private String status;

        public PaymentProcessed() {
            super();
            this.eventType = "PaymentProcessed";
            this.aggregateType = "Payment";
        }

        public String getPaymentReference() {
            return paymentReference;
        }

        public void setPaymentReference(String paymentReference) {
            this.paymentReference = paymentReference;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * Event fired when an invoice is generated
     */
    public static class InvoiceGenerated extends DomainEvent {
        private String invoiceNumber;
        private String customerId;
        private Double totalAmount;
        private String currency;
        private String dueDate;
        private String status;

        public InvoiceGenerated() {
            super();
            this.eventType = "InvoiceGenerated";
            this.aggregateType = "Invoice";
        }

        public String getInvoiceNumber() {
            return invoiceNumber;
        }

        public void setInvoiceNumber(String invoiceNumber) {
            this.invoiceNumber = invoiceNumber;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public Double getTotalAmount() {
            return totalAmount;
        }

        public void setTotalAmount(Double totalAmount) {
            this.totalAmount = totalAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getDueDate() {
            return dueDate;
        }

        public void setDueDate(String dueDate) {
            this.dueDate = dueDate;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    /**
     * Event fired when a quote is provided
     */
    public static class QuoteProvided extends DomainEvent {
        private String quoteNumber;
        private String customerId;
        private Double quotedAmount;
        private String currency;
        private Boolean accepted;
        private String providerId;

        public QuoteProvided() {
            super();
            this.eventType = "QuoteProvided";
            this.aggregateType = "Quote";
        }

        public String getQuoteNumber() {
            return quoteNumber;
        }

        public void setQuoteNumber(String quoteNumber) {
            this.quoteNumber = quoteNumber;
        }

        public String getCustomerId() {
            return customerId;
        }

        public void setCustomerId(String customerId) {
            this.customerId = customerId;
        }

        public Double getQuotedAmount() {
            return quotedAmount;
        }

        public void setQuotedAmount(Double quotedAmount) {
            this.quotedAmount = quotedAmount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public Boolean getAccepted() {
            return accepted;
        }

        public void setAccepted(Boolean accepted) {
            this.accepted = accepted;
        }

        public String getProviderId() {
            return providerId;
        }

        public void setProviderId(String providerId) {
            this.providerId = providerId;
        }
    }
}
