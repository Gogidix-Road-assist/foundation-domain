/**
 * Management Domain Colors - Amazon/Stripe Standards
 * Corporate enterprise colors for Management Domain
 */
export declare const MANAGEMENT_COLORS: {
    readonly primary: "#0066CC";
    readonly primaryHover: "#0052A3";
    readonly primaryLight: "#E6F2FF";
    readonly secondary: "#1A1A1A";
    readonly secondaryLight: "#F5F5F5";
    readonly accent: "#00D4AA";
    readonly accentHover: "#00A888";
    readonly success: "#10B981";
    readonly warning: "#F59E0B";
    readonly error: "#EF4444";
    readonly info: "#3B82F6";
};
export type ManagementColors = typeof MANAGEMENT_COLORS;
/**
 * Business Domain Color Palettes
 * Each domain has primary, secondary, accent, and warmth colors
 */
export declare const BUSINESS_DOMAIN_COLORS: {
    readonly individualInsurance: {
        readonly primary: "#6366F1";
        readonly secondary: "#4F46E5";
        readonly accent: "#8B5CF6";
        readonly warmth: "#F59E0B";
    };
    readonly corporateInsurance: {
        readonly primary: "#0D9488";
        readonly secondary: "#0F766E";
        readonly accent: "#14B8A6";
        readonly warmth: "#F59E0B";
    };
    readonly insuranceCore: {
        readonly primary: "#4338CA";
        readonly secondary: "#3730A3";
        readonly accent: "#6366F1";
        readonly warmth: "#10B981";
    };
    readonly claimsAutomation: {
        readonly primary: "#DC2626";
        readonly secondary: "#B91C1C";
        readonly accent: "#EF4444";
        readonly warmth: "#F59E0B";
    };
    readonly mechanics: {
        readonly primary: "#059669";
        readonly secondary: "#047857";
        readonly accent: "#10B981";
        readonly warmth: "#F59E0B";
    };
    readonly partnersTowing: {
        readonly primary: "#D97706";
        readonly secondary: "#B45309";
        readonly accent: "#FBBF24";
        readonly warmth: "#3B82F6";
    };
    readonly vendorsEcommerce: {
        readonly primary: "#7C3AED";
        readonly secondary: "#6D28D9";
        readonly accent: "#8B5CF6";
        readonly warmth: "#F59E0B";
    };
};
export type BusinessDomain = keyof typeof BUSINESS_DOMAIN_COLORS;
export type BusinessDomainColors = typeof BUSINESS_DOMAIN_COLORS[BusinessDomain];
/**
 * Get domain-specific colors
 */
export declare function getDomainColors(domain: BusinessDomain): BusinessDomainColors;
/**
 * Check if a domain is valid
 */
export declare function isValidDomain(domain: string): domain is BusinessDomain;
//# sourceMappingURL=colors.d.ts.map