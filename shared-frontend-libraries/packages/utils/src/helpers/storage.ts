const STORAGE_KEYS = {
  USER_TOKEN: 'user_token',
  REFRESH_TOKEN: 'refresh_token',
  THEME: 'app_theme',
  LANGUAGE: 'app_language',
  TENANT_ID: 'tenant_id',
};

export const getStorage = (key: string): string | null => {
  if (typeof window !== 'undefined') {
    return localStorage.getItem(key);
  }
  return null;
};

export const setStorage = (key: string, value: string): void => {
  if (typeof window !== 'undefined') {
    localStorage.setItem(key, value);
  }
};

export const removeStorage = (key: string): void => {
  if (typeof window !== 'undefined') {
    localStorage.removeItem(key);
  }
};

export const getSessionStorage = (key: string): any => {
  const value = getStorage(key);
  return value ? JSON.parse(value) : null;
};

export const setSessionStorage = (key: string, value: any): void => {
  setStorage(key, JSON.stringify(value));
};

export const clearAuth = (): void => {
  removeStorage(STORAGE_KEYS.USER_TOKEN);
  removeStorage(STORAGE_KEYS.REFRESH_TOKEN);
};
