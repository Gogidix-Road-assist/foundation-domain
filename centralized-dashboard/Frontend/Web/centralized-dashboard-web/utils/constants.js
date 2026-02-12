// Status constants
export const SERVICE_STATUS = {
  HEALTHY: 'HEALTHY',
  WARNING: 'WARNING',
  CRITICAL: 'CRITICAL',
  UNKNOWN: 'UNKNOWN',
};

export const ALERT_SEVERITY = {
  INFO: 'INFO',
  WARNING: 'WARNING',
  ERROR: 'ERROR',
  CRITICAL: 'CRITICAL',
};

export const ALERT_TYPE = {
  SYSTEM: 'SYSTEM',
  SERVICE: 'SERVICE',
  PERFORMANCE: 'PERFORMANCE',
  SECURITY: 'SECURITY',
};

// Time ranges
export const TIME_RANGES = {
  LAST_HOUR: '1h',
  LAST_6_HOURS: '6h',
  LAST_24_HOURS: '24h',
  LAST_7_DAYS: '7d',
  LAST_30_DAYS: '30d',
  LAST_90_DAYS: '90d',
  CUSTOM: 'custom',
};

// Chart types
export const CHART_TYPES = {
  LINE: 'LINE',
  BAR: 'BAR',
  PIE: 'PIE',
  DONUT: 'DONUT',
  AREA: 'AREA',
};

// Widget types
export const WIDGET_TYPES = {
  METRIC: 'METRIC',
  CHART: 'CHART',
  TABLE: 'TABLE',
  ALERT_LIST: 'ALERT_LIST',
  SERVICE_HEALTH: 'SERVICE_HEALTH',
};

// Pagination defaults
export const PAGINATION = {
  DEFAULT_PAGE: 1,
  DEFAULT_PAGE_SIZE: 10,
  PAGE_SIZES: [10, 20, 50, 100],
};

// Refresh intervals
export const REFRESH_INTERVALS = [
  { value: 5000, label: '5 seconds' },
  { value: 10000, label: '10 seconds' },
  { value: 30000, label: '30 seconds' },
  { value: 60000, label: '1 minute' },
  { value: 300000, label: '5 minutes' },
];

// Color palette
export const COLORS = {
  primary: '#1890ff',
  success: '#52c41a',
  warning: '#faad14',
  error: '#ff4d4f',
  info: '#1890ff',
};

export const CHART_COLORS = [
  '#1890ff',
  '#52c41a',
  '#faad14',
  '#ff4d4f',
  '#722ed1',
  '#eb2f96',
  '#13c2c2',
  '#fa8c16',
];
