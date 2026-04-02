/**
 * Critical Mode Theme (Emergency)
 * High contrast mode for emergency situations
 * All non-critical UI is muted for focus
 */
export const CRITICAL_THEME = {
  palette: {
    mode: 'light' as const,
    critical: {
      main: '#DC2626',
      light: '#FEF2F2',
      dark: '#991B1B',
      contrastText: '#FFFFFF',
    },
    background: {
      default: '#FEF2F2',
      paper: '#FFFFFF',
    },
    text: {
      primary: '#1F2937',
      secondary: '#374151',
      disabled: '#9CA3AF',
    },
    action: {
      active: '#DC2626',
      hover: '#B91C1C',
      selected: '#EF4444',
      disabled: 'rgba(0,0,0,0.3)',
    },
  },
  criticalAction: '#DC2626',
  mutedOpacity: 0.5,
} as const;

export type CriticalTheme = typeof CRITICAL_THEME;
