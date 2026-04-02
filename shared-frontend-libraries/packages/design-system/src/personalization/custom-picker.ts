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
 * Calculate relative luminance of a color
 * @param r - Red (0-255)
 * @param g - Green (0-255)
 * @param b - Blue (0-255)
 * @returns Relative luminance
 */
function getLuminance(r: number, g: number, b: number): number {
  const [rs, gs, bs] = [r, g, b].map(c => {
    const c255 = c / 255;
    return c255 <= 0.03928 ? c255 / 12.92 : Math.pow((c255 + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * rs + 0.7152 * gs + 0.0722 * bs;
}

/**
 * Convert hex color to RGB
 * @param hex - Hex color string
 * @returns RGB values
 */
function hexToRgb(hex: string): { r: number; g: number; b: number } {
  const cleanHex = hex.replace('#', '');
  if (cleanHex.length === 3) {
    return {
      r: parseInt(cleanHex[0] + cleanHex[0], 16),
      g: parseInt(cleanHex[1] + cleanHex[1], 16),
      b: parseInt(cleanHex[2] + cleanHex[2], 16),
    };
  }
  return {
    r: parseInt(cleanHex.substring(0, 2), 16),
    g: parseInt(cleanHex.substring(2, 4), 16),
    b: parseInt(cleanHex.substring(4, 6), 16),
  };
}

/**
 * Calculate contrast ratio between two colors
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Contrast ratio
 */
export function getContrastRatio(foreground: string, background: string): number {
  const fg = hexToRgb(foreground);
  const bg = hexToRgb(background);
  const fgLum = getLuminance(fg.r, fg.g, fg.b);
  const bgLum = getLuminance(bg.r, bg.g, bg.b);
  const lighter = Math.max(fgLum, bgLum);
  const darker = Math.min(fgLum, bgLum);
  return (lighter + 0.05) / (darker + 0.05);
}

/**
 * Validate custom color against WCAG guidelines
 * @param foreground - Foreground color
 * @param background - Background color
 * @returns Validation result
 */
export function validateCustomColor(
  foreground: string,
  background: string
): ColorValidationResult {
  const contrast = getContrastRatio(foreground, background);
  const meetsAA = contrast >= 4.5;
  const meetsAAA = contrast >= 7;

  return {
    valid: meetsAA,
    contrast,
    meetsAA,
    meetsAAA,
  };
}

/**
 * Generate light variant of a color
 * @param hex - Hex color
 * @param factor - Lightness factor (0-1)
 * @returns Lightened color
 */
export function lightenColor(hex: string, factor: number = 0.1): string {
  const rgb = hexToRgb(hex);
  const lightened = {
    r: Math.min(255, Math.round(rgb.r + (255 - rgb.r) * factor)),
    g: Math.min(255, Math.round(rgb.g + (255 - rgb.g) * factor)),
    b: Math.min(255, Math.round(rgb.b + (255 - rgb.b) * factor)),
  };
  return `#${lightened.r.toString(16).padStart(2, '0')}${lightened.g
    .toString(16)
    .padStart(2, '0')}${lightened.b.toString(16).padStart(2, '0')}`;
}

/**
 * Generate dark variant of a color
 * @param hex - Hex color
 * @param factor - Darkness factor (0-1)
 * @returns Darkened color
 */
export function darkenColor(hex: string, factor: number = 0.1): string {
  const rgb = hexToRgb(hex);
  const darkened = {
    r: Math.max(0, Math.round(rgb.r - rgb.r * factor)),
    g: Math.max(0, Math.round(rgb.g - rgb.g * factor)),
    b: Math.max(0, Math.round(rgb.b - rgb.b * factor)),
  };
  return `#${darkened.r.toString(16).padStart(2, '0')}${darkened.g
    .toString(16)
    .padStart(2, '0')}${darkened.b.toString(16).padStart(2, '0')}`;
}
