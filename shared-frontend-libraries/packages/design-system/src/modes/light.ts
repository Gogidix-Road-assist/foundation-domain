import { MANAGEMENT_COLORS } from '../theme/colors';

/**
 * Light Mode Theme
 * Default theme for most applications
 */
export const LIGHT_THEME = {
  palette: {
    mode: 'light' as const,
    primary: {
      main: MANAGEMENT_COLORS.primary,
      light: MANAGEMENT_COLORS.primaryLight,
      dark: MANAGEMENT_COLORS.primaryHover,
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: MANAGEMENT_COLORS.secondary,
      light: MANAGEMENT_COLORS.secondaryLight,
      dark: '#000000',
      contrastText: '#FFFFFF',
    },
    error: {
      main: MANAGEMENT_COLORS.error,
      light: '#F87171',
      dark: '#B91C1C',
      contrastText: '#FFFFFF',
    },
    warning: {
      main: MANAGEMENT_COLORS.warning,
      light: '#FBBF24',
      dark: '#B45309',
      contrastText: '#000000',
    },
    info: {
      main: MANAGEMENT_COLORS.info,
      light: '#60A5FA',
      dark: '#1D4ED8',
      contrastText: '#FFFFFF',
    },
    success: {
      main: MANAGEMENT_COLORS.success,
      light: '#34D399',
      dark: '#047857',
      contrastText: '#FFFFFF',
    },
    text: {
      primary: '#1F2937',
      secondary: '#374151',
      disabled: '#9CA3AF',
    },
    divider: '#E5E7EB',
    background: {
      default: '#FFFFFF',
      paper: '#F9FAFB',
    },
  },
} as const;

export type LightTheme = typeof LIGHT_THEME;
