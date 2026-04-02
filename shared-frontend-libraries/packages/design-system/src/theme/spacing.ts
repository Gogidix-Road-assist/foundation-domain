/**
 * Spacing Scale
 * Base unit: 4px
 * Scale: 0, 0.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
 */
export const SPACING_SCALE = {
  0: '0px',
  0.5: '2px',
  1: '4px',
  2: '8px',
  3: '12px',
  4: '16px',
  5: '20px',
  6: '24px',
  7: '28px',
  8: '32px',
  9: '36px',
  10: '40px',
  11: '44px',
  12: '48px',
  auto: 'auto',
} as const;

export type SpacingScale = keyof typeof SPACING_SCALE;

/**
 * Get spacing value for a given scale
 * @param scale - Spacing scale value
 * @returns CSS spacing value
 */
export function getSpacing(scale: SpacingScale): string {
  return SPACING_SCALE[scale];
}

/**
 * Convert spacing unit to pixels
 * @param scale - Spacing scale value
 * @returns Pixel value (number)
 */
export function spacingToPx(scale: number): number {
  return scale * 4;
}
