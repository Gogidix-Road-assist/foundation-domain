export * from './light';
export * from './dark';
export * from './critical';
export type ThemeMode = 'light' | 'dark' | 'critical';
/**
 * Get theme configuration for a given mode
 * @param mode - Theme mode
 * @returns Theme configuration
 */
export declare function getThemeConfig(mode: ThemeMode): Promise<{
    readonly palette: {
        readonly mode: "light";
        readonly primary: {
            readonly main: "#0066CC";
            readonly light: "#E6F2FF";
            readonly dark: "#0052A3";
            readonly contrastText: "#FFFFFF";
        };
        readonly secondary: {
            readonly main: "#1A1A1A";
            readonly light: "#F5F5F5";
            readonly dark: "#000000";
            readonly contrastText: "#FFFFFF";
        };
        readonly error: {
            readonly main: "#EF4444";
            readonly light: "#F87171";
            readonly dark: "#B91C1C";
            readonly contrastText: "#FFFFFF";
        };
        readonly warning: {
            readonly main: "#F59E0B";
            readonly light: "#FBBF24";
            readonly dark: "#B45309";
            readonly contrastText: "#000000";
        };
        readonly info: {
            readonly main: "#3B82F6";
            readonly light: "#60A5FA";
            readonly dark: "#1D4ED8";
            readonly contrastText: "#FFFFFF";
        };
        readonly success: {
            readonly main: "#10B981";
            readonly light: "#34D399";
            readonly dark: "#047857";
            readonly contrastText: "#FFFFFF";
        };
        readonly text: {
            readonly primary: "#1F2937";
            readonly secondary: "#374151";
            readonly disabled: "#9CA3AF";
        };
        readonly divider: "#E5E7EB";
        readonly background: {
            readonly default: "#FFFFFF";
            readonly paper: "#F9FAFB";
        };
    };
}> | Promise<{
    readonly palette: {
        readonly mode: "dark";
        readonly primary: {
            readonly main: "#0066CC";
            readonly light: "#E6F2FF";
            readonly dark: "#0052A3";
            readonly contrastText: "#FFFFFF";
        };
        readonly secondary: {
            readonly main: "#F5F5F5";
            readonly light: "#9CA3AF";
            readonly dark: "#1A1A1A";
            readonly contrastText: "#000000";
        };
        readonly error: {
            readonly main: "#EF4444";
            readonly light: "#F87171";
            readonly dark: "#B91C1C";
            readonly contrastText: "#FFFFFF";
        };
        readonly warning: {
            readonly main: "#F59E0B";
            readonly light: "#FBBF24";
            readonly dark: "#B45309";
            readonly contrastText: "#000000";
        };
        readonly info: {
            readonly main: "#3B82F6";
            readonly light: "#60A5FA";
            readonly dark: "#1D4ED8";
            readonly contrastText: "#FFFFFF";
        };
        readonly success: {
            readonly main: "#10B981";
            readonly light: "#34D399";
            readonly dark: "#047857";
            readonly contrastText: "#FFFFFF";
        };
        readonly text: {
            readonly primary: "#F9FAFB";
            readonly secondary: "#E5E7EB";
            readonly disabled: "#6B7280";
        };
        readonly divider: "#374151";
        readonly background: {
            readonly default: "#111827";
            readonly paper: "#1F2937";
        };
    };
}> | Promise<{
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
}>;
//# sourceMappingURL=index.d.ts.map