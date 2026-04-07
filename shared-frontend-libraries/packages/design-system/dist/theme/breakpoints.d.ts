/**
 * Responsive Breakpoints
 * Material UI standard breakpoints
 */
export declare const BREAKPOINTS: {
    readonly xs: 0;
    readonly sm: 600;
    readonly md: 900;
    readonly lg: 1200;
    readonly xl: 1536;
    readonly xxl: 1920;
};
export type Breakpoint = keyof typeof BREAKPOINTS;
/**
 * Get breakpoint value in pixels
 * @param breakpoint - Breakpoint name
 * @returns Pixel value
 */
export declare function getBreakpointValue(breakpoint: Breakpoint): number;
/**
 * Get media query for a breakpoint
 * @param breakpoint - Breakpoint name
 * @param type - Media query type (up, down, only)
 * @returns Media query string
 */
export declare function getMediaQuery(breakpoint: Breakpoint, type?: 'up' | 'down' | 'only'): string;
//# sourceMappingURL=breakpoints.d.ts.map