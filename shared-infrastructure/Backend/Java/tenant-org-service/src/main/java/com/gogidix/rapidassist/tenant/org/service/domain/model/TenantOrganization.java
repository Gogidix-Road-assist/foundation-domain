package com.gogidix.rapidassist.tenant.org.service.domain.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "tenant_organizations")
public record TenantOrganization(

        @Id
        String id,

        @Indexed
        @NotBlank(message = "Tenant ID is required")
        String tenantId,

        @Indexed
        @NotBlank(message = "Organization ID is required")
        String orgId,

        @Indexed
        @NotBlank(message = "Organization name is required")
        String orgName,

        @Indexed
        OrganizationType orgType,

        @Indexed
        OrganizationStatus status,

        SubscriptionPlan plan,

        OrganizationSettings settings,

        Map<String, String> metadata,

        BillingInfo billingInfo,

        List<ContactInfo> contacts,

        @Indexed
        Instant createdAt,

        @Indexed
        Instant updatedAt,

        String createdBy,

        String updatedBy,

        @Indexed
        Instant lastActiveAt,

        Boolean isActive,

        String parentOrgId,

        Set<String> tags,

        Map<String, String> customAttributes,

        ComplianceInfo complianceInfo

) {

    public static TenantOrganization create(
            String tenantId, String orgId, String orgName, OrganizationType orgType) {
        Instant now = Instant.now();
        return new TenantOrganization(
                null,
                tenantId,
                orgId,
                orgName,
                orgType,
                OrganizationStatus.ACTIVE,
                new SubscriptionPlan(SubscriptionPlan.PlanType.FREE, null, null, null, null, null),
                new OrganizationSettings(null, null, null, null, null, Map.of()),
                Map.of(),
                null,
                List.of(),
                now,
                now,
                null,
                null,
                now,
                true,
                null,
                Set.of(),
                Map.of(),
                null
        );
    }

    public TenantOrganization withStatus(OrganizationStatus newStatus) {
        return new TenantOrganization(
                id, tenantId, orgId, orgName, orgType, newStatus, plan, settings,
                metadata, billingInfo, contacts, createdAt, Instant.now(),
                createdBy, updatedBy, Instant.now(), isActive, parentOrgId,
                tags, customAttributes, complianceInfo
        );
    }

    public TenantOrganization withPlan(SubscriptionPlan newPlan) {
        return new TenantOrganization(
                id, tenantId, orgId, orgName, orgType, status, newPlan, settings,
                metadata, billingInfo, contacts, createdAt, Instant.now(),
                createdBy, updatedBy, lastActiveAt, isActive, parentOrgId,
                tags, customAttributes, complianceInfo
        );
    }

    public TenantOrganization withSettings(OrganizationSettings newSettings) {
        return new TenantOrganization(
                id, tenantId, orgId, orgName, orgType, status, plan, newSettings,
                metadata, billingInfo, contacts, createdAt, Instant.now(),
                createdBy, updatedBy, lastActiveAt, isActive, parentOrgId,
                tags, customAttributes, complianceInfo
        );
    }

    public boolean isOrganizationActive() {
        return isActive != null && isActive && status == OrganizationStatus.ACTIVE;
    }

    public enum OrganizationType {
        ENTERPRISE, SMB, STARTUP, NON_PROFIT, GOVERNMENT, EDUCATIONAL, INDIVIDUAL
    }

    public enum OrganizationStatus {
        ACTIVE, INACTIVE, SUSPENDED, PENDING_VERIFICATION, DEACTIVATED, LOCKED
    }

    public record SubscriptionPlan(
            PlanType planType,
            Integer maxUsers,
            Integer maxStorageGB,
            Instant startDate,
            Instant endDate,
            Map<String, String> features
    ) {
        public enum PlanType {
            FREE, BASIC, PROFESSIONAL, ENTERPRISE, CUSTOM
        }
    }

    public record OrganizationSettings(
            String timezone,
            String locale,
            String currency,
            String defaultLanguage,
            String notificationEmail,
            Map<String, String> featureFlags
    ) {}

    public record BillingInfo(
            String billingEmail,
            String billingAddress,
            String taxId,
            String paymentMethodId,
            Instant nextBillingDate,
            BillingCycle billingCycle
    ) {
        public enum BillingCycle {
            MONTHLY, QUARTERLY, ANNUAL, CUSTOM
        }
    }

    public record ContactInfo(
            String contactType,
            String name,
            String email,
            String phone,
            Boolean isPrimary
    ) {}

    public record ComplianceInfo(
            Boolean gdprCompliant,
            Boolean hipaaCompliant,
            Boolean soc2Compliant,
            String dataResidency,
            Map<String, Instant> certifications
    ) {}
}
