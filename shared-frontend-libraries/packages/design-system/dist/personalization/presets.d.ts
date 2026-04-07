import { BusinessDomain } from '../brand/business-domains';

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
export declare function getPresetThemes(domain: BusinessDomain): PresetTheme[];
/**
 * Get a specific preset theme
 * @param domain - Business domain
 * @param presetId - Preset theme ID
 * @returns Preset theme or undefined
 */
export declare function getPresetTheme(domain: BusinessDomain, presetId: string): PresetTheme | undefined;
//# sourceMappingURL=presets.d.ts.map