package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for shared request context library.
 *
 * <p>Properties are prefixed with "gogidix.request-context"</p>
 */
@ConfigurationProperties(prefix = "gogidix.request-context")
public class RequestContextProperties {

    private String correlationIdHeader = "X-Correlation-Id";
    private String countryHeader = "X-Country";
    private String tenantIdHeader = "X-Tenant-Id";
    private String userIdHeader = "X-User-Id";
    private String requestIdHeader = "X-Request-Id";

    private boolean requireCountry = false;
    private boolean requireTenantId = true;
    private boolean requireUserId = false;

    private boolean preferJwtClaims = true;
    private boolean enforceHeaderJwtMatch = true;

    private String tenantIdClaim = "tenant_id";
    private String tenantIdClaimFallback = "tenantId";

    private String countryClaim = "country";
    private String countryClaimFallback = "country_code";

    private String userIdClaim = "user_id";
    private String userIdClaimFallback = "userId";

    public String getCorrelationIdHeader() {
        return correlationIdHeader;
    }

    public void setCorrelationIdHeader(String correlationIdHeader) {
        this.correlationIdHeader = correlationIdHeader;
    }

    public String getCountryHeader() {
        return countryHeader;
    }

    public void setCountryHeader(String countryHeader) {
        this.countryHeader = countryHeader;
    }

    public String getTenantIdHeader() {
        return tenantIdHeader;
    }

    public void setTenantIdHeader(String tenantIdHeader) {
        this.tenantIdHeader = tenantIdHeader;
    }

    public String getUserIdHeader() {
        return userIdHeader;
    }

    public void setUserIdHeader(String userIdHeader) {
        this.userIdHeader = userIdHeader;
    }

    public String getRequestIdHeader() {
        return requestIdHeader;
    }

    public void setRequestIdHeader(String requestIdHeader) {
        this.requestIdHeader = requestIdHeader;
    }

    public boolean isRequireCountry() {
        return requireCountry;
    }

    public void setRequireCountry(boolean requireCountry) {
        this.requireCountry = requireCountry;
    }

    public boolean isRequireTenantId() {
        return requireTenantId;
    }

    public void setRequireTenantId(boolean requireTenantId) {
        this.requireTenantId = requireTenantId;
    }

    public boolean isRequireUserId() {
        return requireUserId;
    }

    public void setRequireUserId(boolean requireUserId) {
        this.requireUserId = requireUserId;
    }

    public boolean isPreferJwtClaims() {
        return preferJwtClaims;
    }

    public void setPreferJwtClaims(boolean preferJwtClaims) {
        this.preferJwtClaims = preferJwtClaims;
    }

    public boolean isEnforceHeaderJwtMatch() {
        return enforceHeaderJwtMatch;
    }

    public void setEnforceHeaderJwtMatch(boolean enforceHeaderJwtMatch) {
        this.enforceHeaderJwtMatch = enforceHeaderJwtMatch;
    }

    public String getTenantIdClaim() {
        return tenantIdClaim;
    }

    public void setTenantIdClaim(String tenantIdClaim) {
        this.tenantIdClaim = tenantIdClaim;
    }

    public String getTenantIdClaimFallback() {
        return tenantIdClaimFallback;
    }

    public void setTenantIdClaimFallback(String tenantIdClaimFallback) {
        this.tenantIdClaimFallback = tenantIdClaimFallback;
    }

    public String getCountryClaim() {
        return countryClaim;
    }

    public void setCountryClaim(String countryClaim) {
        this.countryClaim = countryClaim;
    }

    public String getCountryClaimFallback() {
        return countryClaimFallback;
    }

    public void setCountryClaimFallback(String countryClaimFallback) {
        this.countryClaimFallback = countryClaimFallback;
    }

    public String getUserIdClaim() {
        return userIdClaim;
    }

    public void setUserIdClaim(String userIdClaim) {
        this.userIdClaim = userIdClaim;
    }

    public String getUserIdClaimFallback() {
        return userIdClaimFallback;
    }

    public void setUserIdClaimFallback(String userIdClaimFallback) {
        this.userIdClaimFallback = userIdClaimFallback;
    }
}
