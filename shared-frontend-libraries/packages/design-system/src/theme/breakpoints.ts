/**
 * Responsive Breakpoints
 * Material UI standard breakpoints
 */
export const BREAKPOINTS = {
  xs: 0,      // Extra small devices (phones)
  sm: 600,    // Small devices (tablets)
  md: 900,    // Medium devices (small laptops)
  lg: 1200,   // Large devices (desktops)
  xl: 1536,   // Extra large devices (large desktops)
  xxl: 1920,  // Extra extra large devices (4K displays)
} as const;

export type Breakpoint = keyof typeof BREAKPOINTS;

/**
 * Get breakpoint value in pixels
 * @param breakpoint - Breakpoint name
 * @returns Pixel value
 */
export function getBreakpointValue(breakpoint: Breakpoint): number {
  return BREAKPOINTS[breakpoint];
}

/**
 * Get media query for a breakpoint
 * @param breakpoint - Breakpoint name
 * @param type - Media query type (up, down, only)
 * @returns Media query string
 */
export function getMediaQuery(
  breakpoint: Breakpoint,
  type: 'up' | 'down' | 'only' = 'up'
): string {
  const value = BREAKPOINTS[breakpoint];

  switch (type) {
    case 'up':
      return `@media (min-width: ${value}px)`;
    case 'down':
      return `@media (max-width: ${value - 1}px)`;
    case 'only':
      return `@media (min-width: ${value}px) and (max-width: ${BREAKPOINTS[breakpoint as keyof typeof BREAKPOINTS] - 1}px)`;
    default:
      return '';
  }
}
