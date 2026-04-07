const a = {
  primary: "#0066CC",
  // Deep Blue - Primary Action
  primaryHover: "#0052A3",
  // Darker Blue - Hover State
  primaryLight: "#E6F2FF",
  // Light Blue - Backgrounds
  secondary: "#1A1A1A",
  // Dark Gray - Text/Headings
  secondaryLight: "#F5F5F5",
  // Light Gray - Backgrounds
  accent: "#00D4AA",
  // Teal - Success/Confirmation
  accentHover: "#00A888",
  // Darker Teal - Hover
  // Functional colors
  success: "#10B981",
  warning: "#F59E0B",
  error: "#EF4444",
  info: "#3B82F6"
}, F = {
  individualInsurance: {
    primary: "#6366F1",
    // Indigo - Trust & Security
    secondary: "#4F46E5",
    // Deep Indigo - Primary Actions
    accent: "#8B5CF6",
    // Violet - Highlights
    warmth: "#F59E0B"
    // Amber - Personal touch
  },
  corporateInsurance: {
    primary: "#0D9488",
    // Teal - Professional & Trust
    secondary: "#0F766E",
    // Deep Teal - Primary Actions
    accent: "#14B8A6",
    // Lighter Teal - Highlights
    warmth: "#F59E0B"
    // Amber - Business warmth
  },
  insuranceCore: {
    primary: "#4338CA",
    // Deep Indigo - Insurance Authority
    secondary: "#3730A3",
    // Darker Indigo - Actions
    accent: "#6366F1",
    // Blue-Gray - Neutral Highlights
    warmth: "#10B981"
    // Emerald - Claims success
  },
  claimsAutomation: {
    primary: "#DC2626",
    // Deep Red - Urgency & Action
    secondary: "#B91C1C",
    // Darker Red - Primary Actions
    accent: "#EF4444",
    // Lighter Red - Highlights
    warmth: "#F59E0B"
    // Amber - Processing status
  },
  mechanics: {
    primary: "#059669",
    // Emerald Green - Service & Fix
    secondary: "#047857",
    // Deep Emerald - Primary Actions
    accent: "#10B981",
    // Lighter Emerald - Highlights
    warmth: "#F59E0B"
    // Amber - Status updates
  },
  partnersTowing: {
    primary: "#D97706",
    // Amber-Orange - Visibility & Towing
    secondary: "#B45309",
    // Darker Amber - Primary Actions
    accent: "#FBBF24",
    // Lighter Amber - Highlights
    warmth: "#3B82F6"
    // Blue - Trust & Coordination
  },
  vendorsEcommerce: {
    primary: "#7C3AED",
    // Purple - Marketplace & Commerce
    secondary: "#6D28D9",
    // Deep Purple - Primary Actions
    accent: "#8B5CF6",
    // Lighter Purple - Highlights
    warmth: "#F59E0B"
    // Amber - Transaction status
  }
};
function T(r) {
  return F[r];
}
function k(r) {
  return r in F;
}
const h = {
  h1: { fontSize: "2.5rem", fontWeight: 700, lineHeight: 1.2 },
  h2: { fontSize: "2rem", fontWeight: 600, lineHeight: 1.3 },
  h3: { fontSize: "1.75rem", fontWeight: 600, lineHeight: 1.3 },
  h4: { fontSize: "1.5rem", fontWeight: 600, lineHeight: 1.4 },
  h5: { fontSize: "1.25rem", fontWeight: 500, lineHeight: 1.4 },
  h6: { fontSize: "1rem", fontWeight: 500, lineHeight: 1.5 },
  body1: { fontSize: "1rem", fontWeight: 400, lineHeight: 1.5 },
  body2: { fontSize: "0.875rem", fontWeight: 400, lineHeight: 1.5 },
  button: { fontSize: "0.875rem", fontWeight: 500, lineHeight: 1.4 },
  caption: { fontSize: "0.75rem", fontWeight: 400, lineHeight: 1.4 },
  data: { fontSize: "1rem", fontWeight: 600, lineHeight: 1.2 }
  // JetBrains Mono
}, u = {
  ui: '"Inter", -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  data: '"JetBrains Mono", "Fira Code", monospace'
};
function M(r) {
  return h[r];
}
function w() {
  return u.ui;
}
function H() {
  return u.data;
}
const l = {
  0: "0px",
  0.5: "2px",
  1: "4px",
  2: "8px",
  3: "12px",
  4: "16px",
  5: "20px",
  6: "24px",
  7: "28px",
  8: "32px",
  9: "36px",
  10: "40px",
  11: "44px",
  12: "48px",
  auto: "auto"
};
function D(r) {
  return l[r];
}
function _(r) {
  return r * 4;
}
const g = {
  xs: 0,
  // Extra small devices (phones)
  sm: 600,
  // Small devices (tablets)
  md: 900,
  // Medium devices (small laptops)
  lg: 1200,
  // Large devices (desktops)
  xl: 1536,
  // Extra large devices (large desktops)
  xxl: 1920
  // Extra extra large devices (4K displays)
};
function I(r) {
  return g[r];
}
function v(r, t = "up") {
  const e = g[r];
  switch (t) {
    case "up":
      return `@media (min-width: ${e}px)`;
    case "down":
      return `@media (max-width: ${e - 1}px)`;
    case "only":
      return `@media (min-width: ${e}px) and (max-width: ${g[r] - 1}px)`;
    default:
      return "";
  }
}
const y = {
  none: "none",
  xs: "0 1px 2px rgba(0,0,0,0.05)",
  sm: "0 1px 3px rgba(0,0,0,0.1), 0 1px 2px rgba(0,0,0,0.06)",
  md: "0 4px 6px -1px rgba(0,0,0,0.1), 0 2px 4px -1px rgba(0,0,0,0.06)",
  lg: "0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -2px rgba(0,0,0,0.05)",
  xl: "0 20px 25px -5px rgba(0,0,0,0.1), 0 10px 10px -5px rgba(0,0,0,0.04)"
};
function L(r) {
  return y[r];
}
const x = {
  palette: {
    mode: "light",
    primary: {
      main: a.primary,
      light: a.primaryLight,
      dark: a.primaryHover,
      contrastText: "#FFFFFF"
    },
    secondary: {
      main: a.secondary,
      light: a.secondaryLight,
      dark: "#000000",
      contrastText: "#FFFFFF"
    },
    error: {
      main: a.error,
      light: "#F87171",
      dark: "#B91C1C",
      contrastText: "#FFFFFF"
    },
    warning: {
      main: a.warning,
      light: "#FBBF24",
      dark: "#B45309",
      contrastText: "#000000"
    },
    info: {
      main: a.info,
      light: "#60A5FA",
      dark: "#1D4ED8",
      contrastText: "#FFFFFF"
    },
    success: {
      main: a.success,
      light: "#34D399",
      dark: "#047857",
      contrastText: "#FFFFFF"
    },
    text: {
      primary: "#1F2937",
      secondary: "#374151",
      disabled: "#9CA3AF"
    },
    divider: "#E5E7EB",
    background: {
      default: "#FFFFFF",
      paper: "#F9FAFB"
    }
  }
}, m = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  LIGHT_THEME: x
}, Symbol.toStringTag, { value: "Module" })), f = {
  palette: {
    mode: "dark",
    primary: {
      main: a.primary,
      light: a.primaryLight,
      dark: a.primaryHover,
      contrastText: "#FFFFFF"
    },
    secondary: {
      main: a.secondaryLight,
      light: "#9CA3AF",
      dark: "#1A1A1A",
      contrastText: "#000000"
    },
    error: {
      main: a.error,
      light: "#F87171",
      dark: "#B91C1C",
      contrastText: "#FFFFFF"
    },
    warning: {
      main: a.warning,
      light: "#FBBF24",
      dark: "#B45309",
      contrastText: "#000000"
    },
    info: {
      main: a.info,
      light: "#60A5FA",
      dark: "#1D4ED8",
      contrastText: "#FFFFFF"
    },
    success: {
      main: a.success,
      light: "#34D399",
      dark: "#047857",
      contrastText: "#FFFFFF"
    },
    text: {
      primary: "#F9FAFB",
      secondary: "#E5E7EB",
      disabled: "#6B7280"
    },
    divider: "#374151",
    background: {
      default: "#111827",
      paper: "#1F2937"
    }
  }
}, b = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  DARK_THEME: f
}, Symbol.toStringTag, { value: "Module" })), A = {
  palette: {
    mode: "light",
    critical: {
      main: "#DC2626",
      light: "#FEF2F2",
      dark: "#991B1B",
      contrastText: "#FFFFFF"
    },
    background: {
      default: "#FEF2F2",
      paper: "#FFFFFF"
    },
    text: {
      primary: "#1F2937",
      secondary: "#374151",
      disabled: "#9CA3AF"
    },
    action: {
      active: "#DC2626",
      hover: "#B91C1C",
      selected: "#EF4444",
      disabled: "rgba(0,0,0,0.3)"
    }
  },
  criticalAction: "#DC2626",
  mutedOpacity: 0.5
}, E = /* @__PURE__ */ Object.freeze(/* @__PURE__ */ Object.defineProperty({
  __proto__: null,
  CRITICAL_THEME: A
}, Symbol.toStringTag, { value: "Module" }));
function O(r) {
  switch (r) {
    case "light":
      return Promise.resolve().then(() => m).then((t) => t.LIGHT_THEME);
    case "dark":
      return Promise.resolve().then(() => b).then((t) => t.DARK_THEME);
    case "critical":
      return Promise.resolve().then(() => E).then((t) => t.CRITICAL_THEME);
    default:
      return Promise.resolve().then(() => m).then((t) => t.LIGHT_THEME);
  }
}
function B(r) {
  const t = F[r];
  if (!t)
    throw new Error(`Unknown business domain: ${r}`);
  return {
    primary: t.primary,
    secondary: t.secondary,
    accent: t.accent,
    warmth: t.warmth
  };
}
function P() {
  return Object.keys(F);
}
function S(r) {
  const t = B(r);
  return [
    {
      id: "default",
      name: "Default",
      primary: t.primary,
      secondary: t.secondary,
      accent: t.accent,
      background: "#FFFFFF",
      text: "#1F2937"
    },
    {
      id: "professional",
      name: "Professional",
      primary: t.secondary,
      secondary: t.primary,
      accent: t.warmth,
      background: "#F9FAFB",
      text: "#111827"
    },
    {
      id: "warm",
      name: "Warm",
      primary: t.warmth,
      secondary: t.primary,
      accent: t.accent,
      background: "#FFFBEB",
      text: "#1F2937"
    },
    {
      id: "cool",
      name: "Cool",
      primary: t.accent,
      secondary: t.secondary,
      accent: t.primary,
      background: "#F0F9FF",
      text: "#1F2937"
    },
    {
      id: "high-contrast",
      name: "High Contrast",
      primary: "#000000",
      secondary: "#FFFFFF",
      accent: t.primary,
      background: "#FFFFFF",
      text: "#000000"
    },
    {
      id: "dark-focus",
      name: "Dark Focus",
      primary: t.primary,
      secondary: "#1F2937",
      accent: t.accent,
      background: "#111827",
      text: "#F9FAFB"
    }
  ];
}
function z(r, t) {
  return S(r).find((n) => n.id === t);
}
function p(r, t, e) {
  const [n, o, c] = [r, t, e].map((d) => {
    const i = d / 255;
    return i <= 0.03928 ? i / 12.92 : Math.pow((i + 0.055) / 1.055, 2.4);
  });
  return 0.2126 * n + 0.7152 * o + 0.0722 * c;
}
function s(r) {
  const t = r.replace("#", "");
  return t.length === 3 ? {
    r: parseInt(t[0] + t[0], 16),
    g: parseInt(t[1] + t[1], 16),
    b: parseInt(t[2] + t[2], 16)
  } : {
    r: parseInt(t.substring(0, 2), 16),
    g: parseInt(t.substring(2, 4), 16),
    b: parseInt(t.substring(4, 6), 16)
  };
}
function C(r, t) {
  const e = s(r), n = s(t), o = p(e.r, e.g, e.b), c = p(n.r, n.g, n.b), d = Math.max(o, c), i = Math.min(o, c);
  return (d + 0.05) / (i + 0.05);
}
function W(r, t) {
  const e = C(r, t), n = e >= 4.5, o = e >= 7;
  return {
    valid: n,
    contrast: e,
    meetsAA: n,
    meetsAAA: o
  };
}
function R(r, t = 0.1) {
  const e = s(r), n = {
    r: Math.min(255, Math.round(e.r + (255 - e.r) * t)),
    g: Math.min(255, Math.round(e.g + (255 - e.g) * t)),
    b: Math.min(255, Math.round(e.b + (255 - e.b) * t))
  };
  return `#${n.r.toString(16).padStart(2, "0")}${n.g.toString(16).padStart(2, "0")}${n.b.toString(16).padStart(2, "0")}`;
}
function $(r, t = 0.1) {
  const e = s(r), n = {
    r: Math.max(0, Math.round(e.r - e.r * t)),
    g: Math.max(0, Math.round(e.g - e.g * t)),
    b: Math.max(0, Math.round(e.b - e.b * t))
  };
  return `#${n.r.toString(16).padStart(2, "0")}${n.g.toString(16).padStart(2, "0")}${n.b.toString(16).padStart(2, "0")}`;
}
export {
  g as BREAKPOINTS,
  F as BUSINESS_DOMAIN_COLORS,
  A as CRITICAL_THEME,
  f as DARK_THEME,
  u as FONT_FAMILIES,
  x as LIGHT_THEME,
  a as MANAGEMENT_COLORS,
  y as SHADOWS,
  l as SPACING_SCALE,
  h as TYPOGRAPHY_SCALE,
  $ as darkenColor,
  P as getAvailableBusinessDomains,
  I as getBreakpointValue,
  B as getBusinessDomainBrandColors,
  C as getContrastRatio,
  H as getDataFontFamily,
  T as getDomainColors,
  v as getMediaQuery,
  z as getPresetTheme,
  S as getPresetThemes,
  L as getShadow,
  D as getSpacing,
  O as getThemeConfig,
  M as getTypographyScale,
  w as getUIFontFamily,
  k as isValidDomain,
  R as lightenColor,
  _ as spacingToPx,
  W as validateCustomColor
};
