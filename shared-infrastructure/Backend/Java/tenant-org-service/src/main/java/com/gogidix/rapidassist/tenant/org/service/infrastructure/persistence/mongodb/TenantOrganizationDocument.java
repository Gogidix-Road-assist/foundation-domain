package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.tenant.org.service.domain.model.TenantOrganization;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Document(collection = "tenant_organizations")
public record TenantOrganizationDocument(
        @Id
        String id,
        @Indexed
        String tenantId,
        @Indexed
        String orgId,
        @Indexed
        String orgName,
        @Indexed
        String orgType,
        @Indexed
        String status,
        SubscriptionPlanData plan,
        OrganizationSettingsData settings,
        Map<String, String> metadata,
        BillingInfoData billingInfo,
        List<ContactInfoData> contacts,
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
        ComplianceInfoData complianceInfo
) {

    public static TenantOrganizationDocument fromDomain(TenantOrganization organization) {
        return new TenantOrganizationDocument(
                organization.id(),
                organization.tenantId(),
                organization.orgId(),
                organization.orgName(),
                organization.orgType() != null ? organization.orgType().name() : null,
                organization.status() != null ? organization.status().name() : null,
                organization.plan() != null ? new SubscriptionPlanData(organization.plan()) : null,
                organization.settings() != null ? new OrganizationSettingsData(organization.settings()) : null,
                organization.metadata(),
                organization.billingInfo() != null ? new BillingInfoData(organization.billingInfo()) : null,
                organization.contacts() != null ? organization.contacts().stream()
                        .map(ContactInfoData::new)
                        .toList() : List.of(),
                organization.createdAt(),
                organization.updatedAt(),
                organization.createdBy(),
                organization.updatedBy(),
                organization.lastActiveAt(),
                organization.isActive(),
                organization.parentOrgId(),
                organization.tags(),
                organization.customAttributes(),
                organization.complianceInfo() != null ? new ComplianceInfoData(organization.complianceInfo()) : null
        );
    }

    public TenantOrganization toDomain() {
        return new TenantOrganization(
                id,
                tenantId,
                orgId,
                orgName,
                orgType != null ? TenantOrganization.OrganizationType.valueOf(orgType) : null,
                status != null ? TenantOrganization.OrganizationStatus.valueOf(status) : null,
                plan != null ? plan.toDomain() : null,
                settings != null ? settings.toDomain() : null,
                metadata,
                billingInfo != null ? billingInfo.toDomain() : null,
                contacts != null ? contacts.stream()
                        .map(ContactInfoData::toDomain)
                        .toList() : List.of(),
                createdAt,
                updatedAt,
                createdBy,
                updatedBy,
                lastActiveAt,
                isActive,
                parentOrgId,
                tags,
                customAttributes,
                complianceInfo != null ? complianceInfo.toDomain() : null
        );
    }

    public record SubscriptionPlanData(
            String planType,
            Integer maxUsers,
            Integer maxStorageGB,
            Instant startDate,
            Instant endDate,
            Map<String, String> features
    ) {
        public SubscriptionPlanData(TenantOrganization.SubscriptionPlan plan) {
                this(
                        plan.planType() != null ? plan.planType().name() : null,
                        plan.maxUsers(),
                        plan.maxStorageGB(),
                        plan.startDate(),
                        plan.endDate(),
                        plan.features()
                );
        }

        public TenantOrganization.SubscriptionPlan toDomain() {
                return new TenantOrganization.SubscriptionPlan(
                        planType != null ? TenantOrganization.SubscriptionPlan.PlanType.valueOf(planType) : null,
                        maxUsers,
                        maxStorageGB,
                        startDate,
                        endDate,
                        features
                );
        }
    }

    public record OrganizationSettingsData(
            String timezone,
            String locale,
            String currency,
            String defaultLanguage,
            String notificationEmail,
            Map<String, String> featureFlags
    ) {
        public OrganizationSettingsData(TenantOrganization.OrganizationSettings settings) {
                this(
                        settings.timezone(),
                        settings.locale(),
                        settings.currency(),
                        settings.defaultLanguage(),
                        settings.notificationEmail(),
                        settings.featureFlags()
                );
        }

        public TenantOrganization.OrganizationSettings toDomain() {
                return new TenantOrganization.OrganizationSettings(
                        timezone,
                        locale,
                        currency,
                        defaultLanguage,
                        notificationEmail,
                        featureFlags
                );
        }
    }

    public record BillingInfoData(
            String billingEmail,
            String billingAddress,
            String taxId,
            String paymentMethodId,
            Instant nextBillingDate,
            String billingCycle
    ) {
        public BillingInfoData(TenantOrganization.BillingInfo billingInfo) {
                this(
                        billingInfo.billingEmail(),
                        billingInfo.billingAddress(),
                        billingInfo.taxId(),
                        billingInfo.paymentMethodId(),
                        billingInfo.nextBillingDate(),
                        billingInfo.billingCycle() != null ? billingInfo.billingCycle().name() : null
                );
        }

        public TenantOrganization.BillingInfo toDomain() {
                return new TenantOrganization.BillingInfo(
                        billingEmail,
                        billingAddress,
                        taxId,
                        paymentMethodId,
                        nextBillingDate,
                        billingCycle != null ? TenantOrganization.BillingInfo.BillingCycle.valueOf(billingCycle) : null
                );
        }
    }

    public record ContactInfoData(
            String contactType,
            String name,
            String email,
            String phone,
            Boolean isPrimary
    ) {
        public ContactInfoData(TenantOrganization.ContactInfo contactInfo) {
                this(
                        contactInfo.contactType(),
                        contactInfo.name(),
                        contactInfo.email(),
                        contactInfo.phone(),
                        contactInfo.isPrimary()
                );
        }

        public TenantOrganization.ContactInfo toDomain() {
                return new TenantOrganization.ContactInfo(
                        contactType,
                        name,
                        email,
                        phone,
                        isPrimary
                );
        }
    }

    public record ComplianceInfoData(
            Boolean gdprCompliant,
            Boolean hipaaCompliant,
            Boolean soc2Compliant,
            String dataResidency,
            Map<String, Instant> certifications
    ) {
        public ComplianceInfoData(TenantOrganization.ComplianceInfo complianceInfo) {
                this(
                        complianceInfo.gdprCompliant(),
                        complianceInfo.hipaaCompliant(),
                        complianceInfo.soc2Compliant(),
                        complianceInfo.dataResidency(),
                        complianceInfo.certifications()
                );
        }

        public TenantOrganization.ComplianceInfo toDomain() {
                return new TenantOrganization.ComplianceInfo(
                        gdprCompliant,
                        hipaaCompliant,
                        soc2Compliant,
                        dataResidency,
                        certifications
                );
        }
    }
}
