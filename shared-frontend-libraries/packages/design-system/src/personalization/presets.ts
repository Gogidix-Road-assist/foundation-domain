import { getBusinessDomainBrandColors, BusinessDomain } from '../brand/business-domains';

/**
 * Preset Themes
 * 4-6 pre-built themes per domain with WCAG AA contrast compliance
 */

export interface PresetTheme {
  id: string;
  name: string;
  primary: string;
  secondary: string;
  accent: string;
  background: string;
  text: string;
}

/**
 * Generate preset themes for a business domain
 * @param domain - Business domain
 * @returns Array of preset themes
 */
export function getPresetThemes(domain: BusinessDomain): PresetTheme[] {
  const baseColors = getBusinessDomainBrandColors(domain);

  return [
    {
      id: 'default',
      name: 'Default',
      primary: baseColors.primary,
      secondary: baseColors.secondary,
      accent: baseColors.accent,
      background: '#FFFFFF',
      text: '#1F2937',
    },
    {
      id: 'professional',
      name: 'Professional',
      primary: baseColors.secondary,
      secondary: baseColors.primary,
      accent: baseColors.warmth,
      background: '#F9FAFB',
      text: '#111827',
    },
    {
      id: 'warm',
      name: 'Warm',
      primary: baseColors.warmth,
      secondary: baseColors.primary,
      accent: baseColors.accent,
      background: '#FFFBEB',
      text: '#1F2937',
    },
    {
      id: 'cool',
      name: 'Cool',
      primary: baseColors.accent,
      secondary: baseColors.secondary,
      accent: baseColors.primary,
      background: '#F0F9FF',
      text: '#1F2937',
    },
    {
      id: 'high-contrast',
      name: 'High Contrast',
      primary: '#000000',
      secondary: '#FFFFFF',
      accent: baseColors.primary,
      background: '#FFFFFF',
      text: '#000000',
    },
    {
      id: 'dark-focus',
      name: 'Dark Focus',
      primary: baseColors.primary,
      secondary: '#1F2937',
      accent: baseColors.accent,
      background: '#111827',
      text: '#F9FAFB',
    },
  ];
}

/**
 * Get a specific preset theme
 * @param domain - Business domain
 * @param presetId - Preset theme ID
 * @returns Preset theme or undefined
 */
export function getPresetTheme(
  domain: BusinessDomain,
  presetId: string
): PresetTheme | undefined {
  const presets = getPresetThemes(domain);
  return presets.find(p => p.id === presetId);
}
