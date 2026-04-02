/**
 * Typography Scale
 * Enterprise typography system with Inter for UI text and JetBrains Mono for data
 */
export const TYPOGRAPHY_SCALE = {
  h1: { fontSize: '2.5rem', fontWeight: 700, lineHeight: 1.2 },
  h2: { fontSize: '2rem', fontWeight: 600, lineHeight: 1.3 },
  h3: { fontSize: '1.75rem', fontWeight: 600, lineHeight: 1.3 },
  h4: { fontSize: '1.5rem', fontWeight: 600, lineHeight: 1.4 },
  h5: { fontSize: '1.25rem', fontWeight: 500, lineHeight: 1.4 },
  h6: { fontSize: '1rem', fontWeight: 500, lineHeight: 1.5 },
  body1: { fontSize: '1rem', fontWeight: 400, lineHeight: 1.5 },
  body2: { fontSize: '0.875rem', fontWeight: 400, lineHeight: 1.5 },
  button: { fontSize: '0.875rem', fontWeight: 500, lineHeight: 1.4 },
  caption: { fontSize: '0.75rem', fontWeight: 400, lineHeight: 1.4 },
  data: { fontSize: '1rem', fontWeight: 600, lineHeight: 1.2 }, // JetBrains Mono
} as const;

export type TypographyScale = keyof typeof TYPOGRAPHY_SCALE;

/**
 * Font families
 */
export const FONT_FAMILIES = {
  ui: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  data: '"JetBrains Mono", "Fira Code", monospace',
} as const;

/**
 * Get typography configuration for a given scale
 */
export function getTypographyScale(scale: TypographyScale) {
  return TYPOGRAPHY_SCALE[scale];
}

/**
 * Get font family for UI elements
 */
export function getUIFontFamily(): string {
  return FONT_FAMILIES.ui;
}

/**
 * Get font family for data displays
 */
export function getDataFontFamily(): string {
  return FONT_FAMILIES.data;
}
