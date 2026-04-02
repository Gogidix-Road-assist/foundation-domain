import { BUSINESS_DOMAIN_COLORS } from '../theme/colors';

/**
 * Business Domain Brand Colors
 * Extends base domain colors with brand-specific properties
 */

export type BusinessDomain =
  | 'individualInsurance'
  | 'corporateInsurance'
  | 'insuranceCore'
  | 'claimsAutomation'
  | 'mechanics'
  | 'partnersTowing'
  | 'vendorsEcommerce';

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
export function getBusinessDomainBrandColors(
  domain: BusinessDomain
): BusinessDomainBrandColors {
  const colors = BUSINESS_DOMAIN_COLORS[domain];

  if (!colors) {
    throw new Error(`Unknown business domain: ${domain}`);
  }

  return {
    primary: colors.primary,
    secondary: colors.secondary,
    accent: colors.accent,
    warmth: colors.warmth,
  };
}

/**
 * Get all available business domains
 * @returns Array of business domain names
 */
export function getAvailableBusinessDomains(): BusinessDomain[] {
  return Object.keys(BUSINESS_DOMAIN_COLORS) as BusinessDomain[];
}
