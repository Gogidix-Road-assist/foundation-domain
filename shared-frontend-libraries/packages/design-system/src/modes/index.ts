export * from './light';
export * from './dark';
export * from './critical';

export type ThemeMode = 'light' | 'dark' | 'critical';

/**
 * Get theme configuration for a given mode
 * @param mode - Theme mode
 * @returns Theme configuration
 */
export function getThemeConfig(mode: ThemeMode) {
  switch (mode) {
    case 'light':
      return import('./light').then(m => m.LIGHT_THEME);
    case 'dark':
      return import('./dark').then(m => m.DARK_THEME);
    case 'critical':
      return import('./critical').then(m => m.CRITICAL_THEME);
    default:
      return import('./light').then(m => m.LIGHT_THEME);
  }
}
