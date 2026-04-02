import { MANAGEMENT_COLORS } from '../theme/colors';

/**
 * Dark Mode Theme
 * Reduced eye strain for low-light environments
 */
export const DARK_THEME = {
  palette: {
    mode: 'dark' as const,
    primary: {
      main: MANAGEMENT_COLORS.primary,
      light: MANAGEMENT_COLORS.primaryLight,
      dark: MANAGEMENT_COLORS.primaryHover,
      contrastText: '#FFFFFF',
    },
    secondary: {
      main: MANAGEMENT_COLORS.secondaryLight,
      light: '#9CA3AF',
      dark: '#1A1A1A',
      contrastText: '#000000',
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
      primary: '#F9FAFB',
      secondary: '#E5E7EB',
      disabled: '#6B7280',
    },
    divider: '#374151',
    background: {
      default: '#111827',
      paper: '#1F2937',
    },
  },
} as const;

export type DarkTheme = typeof DARK_THEME;
