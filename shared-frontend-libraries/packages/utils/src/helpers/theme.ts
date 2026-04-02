import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ThemeMode = 'light' | 'dark' | 'critical';

export interface ThemeConfig {
  mode: ThemeMode;
  primary: string;
  secondary: string;
  accent: string;
  background: string;
  text: string;
}

export const getTheme = (): ThemeConfig => {
  const storedMode = getStorage('app_theme') as ThemeMode || 'light';
  const tenantId = getStorage(STORAGE_KEYS.TENANT_ID);

  if (tenantId === '1') {
    return {
      mode: 'light',
      primary: MANAGEMENT_COLORS.primary,
      secondary: MANAGEMENT_COLORS.secondary,
      accent: MANAGEMENT_COLORS.accent,
      background: '#FFFFFF',
      text: '#111827',
    };
  }

  if (tenantId === '2') {
    return {
      mode: 'light',
      primary: '#10B981',
      secondary: '#6B7280',
      accent: '#F59E0B',
      background: '#FFFFFF',
      text: '#111827',
    };
  }

  if (tenantId === '3') {
    return {
      mode: 'dark',
      primary: MANAGEMENT_COLORS.primaryLight,
      secondary: '#374151',
      accent: MANAGEMENT_COLORS.accentLight,
      background: '#1A1A1A1',
      text: '#E5E7EB',
    };
  }

  if (tenantId === '4') {
    return {
      mode: 'critical',
      primary: MANAGEMENT_COLORS.error,
      secondary: MANAGEMENT_COLORS.warning,
      accent: MANAGEMENT_COLORS.warning,
      background: '#7F1D1D',
      text: '#FEF2F2',
    };
  }

  return {
    mode: 'light',
    primary: MANAGEMENT_COLORS.primary,
    secondary: MANAGEMENT_COLORS.secondary,
    accent: MANAGEMENT_COLORS.accent,
    background: '#FFFFFF',
    text: '#111827',
  };
};

export const setTheme = (mode: ThemeMode): void => {
  setStorage('app_theme', mode);
};

export const toggleTheme = (): ThemeMode => {
  const current = getTheme().mode;
  const modes: ThemeMode[] = ['light', 'dark', 'critical'];
  const currentIndex = modes.indexOf(current);
  const nextIndex = (currentIndex + 1) % modes.length;
  setTheme(modes[nextIndex]);
};

export const getBusinessDomainTheme = (domainId: string): ThemeConfig => {
  if (domainId === '1') {
    return getTheme();
  }

  const domainColors: Record<string, ThemeConfig> = {
    '1': getTheme(),
    '2': getTheme(),
    '3': getTheme(),
    '4': getTheme(),
    '5': getTheme(),
    '6': getTheme(),
    '7': getTheme(),
  };

  return domainColors[domainId] || getTheme();
};
