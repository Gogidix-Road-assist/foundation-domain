/**
 * Critical Mode Theme (Emergency)
 * High contrast mode for emergency situations
 * All non-critical UI is muted for focus
 */
export declare const CRITICAL_THEME: {
    readonly palette: {
        readonly mode: "light";
        readonly critical: {
            readonly main: "#DC2626";
            readonly light: "#FEF2F2";
            readonly dark: "#991B1B";
            readonly contrastText: "#FFFFFF";
        };
        readonly background: {
            readonly default: "#FEF2F2";
            readonly paper: "#FFFFFF";
        };
        readonly text: {
            readonly primary: "#1F2937";
            readonly secondary: "#374151";
            readonly disabled: "#9CA3AF";
        };
        readonly action: {
            readonly active: "#DC2626";
            readonly hover: "#B91C1C";
            readonly selected: "#EF4444";
            readonly disabled: "rgba(0,0,0,0.3)";
        };
    };
    readonly criticalAction: "#DC2626";
    readonly mutedOpacity: 0.5;
};
export type CriticalTheme = typeof CRITICAL_THEME;
//# sourceMappingURL=critical.d.ts.map