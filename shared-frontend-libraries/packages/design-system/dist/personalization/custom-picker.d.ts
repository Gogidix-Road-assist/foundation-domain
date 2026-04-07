/**
 * Custom Color Picker
 * Validates custom colors against WCAG AA contrast guidelines (4.5:1 minimum)
 */
export interface ColorValidationResult {
    valid: boolean;
    contrast: number;
    meetsAA: boolean;
    meetsAAA: boolean;
}
/**
 * Calculate contrast ratio between two colors
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Contrast ratio
 */
export declare function getContrastRatio(foreground: string, background: string): number;
/**
 * Validate custom color against WCAG guidelines
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Validation result
 */
export declare function validateCustomColor(foreground: string, background: string): ColorValidationResult;
/**
 * Generate light variant of a color
 * @param hex - Hex color
 * @param factor - Lightness factor (0-1)
 * @returns Lightened color
 */
export declare function lightenColor(hex: string, factor?: number): string;
/**
 * Generate dark variant of a color
 * @param hex - Hex color
 * @param factor - Darkness factor (0-1)
 * @returns Darkened color
 */
export declare function darkenColor(hex: string, factor?: number): string;
//# sourceMappingURL=custom-picker.d.ts.map