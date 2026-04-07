/**
 * Typography Scale
 * Enterprise typography system with Inter for UI text and JetBrains Mono for data
 */
export declare const TYPOGRAPHY_SCALE: {
    readonly h1: {
        readonly fontSize: "2.5rem";
        readonly fontWeight: 700;
        readonly lineHeight: 1.2;
    };
    readonly h2: {
        readonly fontSize: "2rem";
        readonly fontWeight: 600;
        readonly lineHeight: 1.3;
    };
    readonly h3: {
        readonly fontSize: "1.75rem";
        readonly fontWeight: 600;
        readonly lineHeight: 1.3;
    };
    readonly h4: {
        readonly fontSize: "1.5rem";
        readonly fontWeight: 600;
        readonly lineHeight: 1.4;
    };
    readonly h5: {
        readonly fontSize: "1.25rem";
        readonly fontWeight: 500;
        readonly lineHeight: 1.4;
    };
    readonly h6: {
        readonly fontSize: "1rem";
        readonly fontWeight: 500;
        readonly lineHeight: 1.5;
    };
    readonly body1: {
        readonly fontSize: "1rem";
        readonly fontWeight: 400;
        readonly lineHeight: 1.5;
    };
    readonly body2: {
        readonly fontSize: "0.875rem";
        readonly fontWeight: 400;
        readonly lineHeight: 1.5;
    };
    readonly button: {
        readonly fontSize: "0.875rem";
        readonly fontWeight: 500;
        readonly lineHeight: 1.4;
    };
    readonly caption: {
        readonly fontSize: "0.75rem";
        readonly fontWeight: 400;
        readonly lineHeight: 1.4;
    };
    readonly data: {
        readonly fontSize: "1rem";
        readonly fontWeight: 600;
        readonly lineHeight: 1.2;
    };
};
export type TypographyScale = keyof typeof TYPOGRAPHY_SCALE;
/**
 * Font families
 */
export declare const FONT_FAMILIES: {
    readonly ui: "\"Inter\", -apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, sans-serif";
    readonly data: "\"JetBrains Mono\", \"Fira Code\", monospace";
};
/**
 * Get typography configuration for a given scale
 */
export declare function getTypographyScale(scale: TypographyScale): {
    readonly fontSize: "2.5rem";
    readonly fontWeight: 700;
    readonly lineHeight: 1.2;
} | {
    readonly fontSize: "2rem";
    readonly fontWeight: 600;
    readonly lineHeight: 1.3;
} | {
    readonly fontSize: "1.75rem";
    readonly fontWeight: 600;
    readonly lineHeight: 1.3;
} | {
    readonly fontSize: "1.5rem";
    readonly fontWeight: 600;
    readonly lineHeight: 1.4;
} | {
    readonly fontSize: "1.25rem";
    readonly fontWeight: 500;
    readonly lineHeight: 1.4;
} | {
    readonly fontSize: "1rem";
    readonly fontWeight: 500;
    readonly lineHeight: 1.5;
} | {
    readonly fontSize: "1rem";
    readonly fontWeight: 400;
    readonly lineHeight: 1.5;
} | {
    readonly fontSize: "0.875rem";
    readonly fontWeight: 400;
    readonly lineHeight: 1.5;
} | {
    readonly fontSize: "0.875rem";
    readonly fontWeight: 500;
    readonly lineHeight: 1.4;
} | {
    readonly fontSize: "0.75rem";
    readonly fontWeight: 400;
    readonly lineHeight: 1.4;
} | {
    readonly fontSize: "1rem";
    readonly fontWeight: 600;
    readonly lineHeight: 1.2;
};
/**
 * Get font family for UI elements
 */
export declare function getUIFontFamily(): string;
/**
 * Get font family for data displays
 */
export declare function getDataFontFamily(): string;
//# sourceMappingURL=typography.d.ts.map