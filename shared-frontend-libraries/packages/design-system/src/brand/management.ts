/**
 * Management Domain Brand Colors
 * Amazon/Stripe standards for corporate enterprise
 */
export const MANAGEMENT_BRAND_COLORS = {
  primary: '#0066CC',
  primaryHover: '#0052A3',
  primaryLight: '#E6F2FF',
  secondary: '#1A1A1A',
  secondaryLight: '#F5F5F5',
  accent: '#00D4AA',
  accentHover: '#00A888',
} as const;

/**
 * Get Management Domain brand colors
 * @returns Brand color configuration
 */
export function getManagementBrandColors() {
  return MANAGEMENT_BRAND_COLORS;
}
