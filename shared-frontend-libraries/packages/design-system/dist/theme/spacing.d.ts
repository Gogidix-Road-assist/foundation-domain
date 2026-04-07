/**
 * Spacing Scale
 * Base unit: 4px
 * Scale: 0, 0.5, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
 */
export declare const SPACING_SCALE: {
    readonly 0: "0px";
    readonly 0.5: "2px";
    readonly 1: "4px";
    readonly 2: "8px";
    readonly 3: "12px";
    readonly 4: "16px";
    readonly 5: "20px";
    readonly 6: "24px";
    readonly 7: "28px";
    readonly 8: "32px";
    readonly 9: "36px";
    readonly 10: "40px";
    readonly 11: "44px";
    readonly 12: "48px";
    readonly auto: "auto";
};
export type SpacingScale = keyof typeof SPACING_SCALE;
/**
 * Get spacing value for a given scale
 * @param scale - Spacing scale value
 * @returns CSS spacing value
 */
export declare function getSpacing(scale: SpacingScale): string;
/**
 * Convert spacing unit to pixels
 * @param scale - Spacing scale value
 * @returns Pixel value (number)
 */
export declare function spacingToPx(scale: number): number;
//# sourceMappingURL=spacing.d.ts.map