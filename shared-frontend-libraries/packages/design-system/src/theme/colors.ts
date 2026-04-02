/**
 * Management Domain Colors - Amazon/Stripe Standards
 * Corporate enterprise colors for Management Domain
 */
export const MANAGEMENT_COLORS = {
  primary: '#0066CC',        // Deep Blue - Primary Action
  primaryHover: '#0052A3',   // Darker Blue - Hover State
  primaryLight: '#E6F2FF',    // Light Blue - Backgrounds
  secondary: '#1A1A1A',       // Dark Gray - Text/Headings
  secondaryLight: '#F5F5F5',   // Light Gray - Backgrounds
  accent: '#00D4AA',          // Teal - Success/Confirmation
  accentHover: '#00A888',     // Darker Teal - Hover

  // Functional colors
  success: '#10B981',
  warning: '#F59E0B',
  error: '#EF4444',
  info: '#3B82F6',
} as const;

export type ManagementColors = typeof MANAGEMENT_COLORS;

/**
 * Business Domain Color Palettes
 * Each domain has primary, secondary, accent, and warmth colors
 */
export const BUSINESS_DOMAIN_COLORS = {
  individualInsurance: {
    primary: '#6366F1',        // Indigo - Trust & Security
    secondary: '#4F46E5',      // Deep Indigo - Primary Actions
    accent: '#8B5CF6',          // Violet - Highlights
    warmth: '#F59E0B',          // Amber - Personal touch
  },
  corporateInsurance: {
    primary: '#0D9488',        // Teal - Professional & Trust
    secondary: '#0F766E',      // Deep Teal - Primary Actions
    accent: '#14B8A6',          // Lighter Teal - Highlights
    warmth: '#F59E0B',          // Amber - Business warmth
  },
  insuranceCore: {
    primary: '#4338CA',        // Deep Indigo - Insurance Authority
    secondary: '#3730A3',      // Darker Indigo - Actions
    accent: '#6366F1',          // Blue-Gray - Neutral Highlights
    warmth: '#10B981',          // Emerald - Claims success
  },
  claimsAutomation: {
    primary: '#DC2626',        // Deep Red - Urgency & Action
    secondary: '#B91C1C',      // Darker Red - Primary Actions
    accent: '#EF4444',          // Lighter Red - Highlights
    warmth: '#F59E0B',          // Amber - Processing status
  },
  mechanics: {
    primary: '#059669',        // Emerald Green - Service & Fix
    secondary: '#047857',      // Deep Emerald - Primary Actions
    accent: '#10B981',          // Lighter Emerald - Highlights
    warmth: '#F59E0B',          // Amber - Status updates
  },
  partnersTowing: {
    primary: '#D97706',        // Amber-Orange - Visibility & Towing
    secondary: '#B45309',      // Darker Amber - Primary Actions
    accent: '#FBBF24',          // Lighter Amber - Highlights
    warmth: '#3B82F6',          // Blue - Trust & Coordination
  },
  vendorsEcommerce: {
    primary: '#7C3AED',        // Purple - Marketplace & Commerce
    secondary: '#6D28D9',      // Deep Purple - Primary Actions
    accent: '#8B5CF6',          // Lighter Purple - Highlights
    warmth: '#F59E0B',          // Amber - Transaction status
  },
} as const;

export type BusinessDomain = keyof typeof BUSINESS_DOMAIN_COLORS;
export type BusinessDomainColors = typeof BUSINESS_DOMAIN_COLORS[BusinessDomain];

/**
 * Get domain-specific colors
 */
export function getDomainColors(domain: BusinessDomain): BusinessDomainColors {
  return BUSINESS_DOMAIN_COLORS[domain];
}

/**
 * Check if a domain is valid
 */
export function isValidDomain(domain: string): domain is BusinessDomain {
  return domain in BUSINESS_DOMAIN_COLORS;
}
