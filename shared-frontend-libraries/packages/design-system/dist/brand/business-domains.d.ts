/**
 * Business Domain Brand Colors
 * Extends base domain colors with brand-specific properties
 */
export type BusinessDomain = 'individualInsurance' | 'corporateInsurance' | 'insuranceCore' | 'claimsAutomation' | 'mechanics' | 'partnersTowing' | 'vendorsEcommerce';
export interface BusinessDomainBrandColors {
    primary: string;
    secondary: string;
    accent: string;
    warmth: string;
}
/**
 * Get brand colors for a specific business domain
 * @param domain - Business domain name
 * @returns Brand color configuration
 */
export declare function getBusinessDomainBrandColors(domain: BusinessDomain): BusinessDomainBrandColors;
/**
 * Get all available business domains
 * @returns Array of business domain names
 */
export declare function getAvailableBusinessDomains(): BusinessDomain[];
//# sourceMappingURL=business-domains.d.ts.map