/**
 * @rapid-assist/shared-frontend
 *
 * Foundation-level shared UI components, design system, and utilities
 * used across all domains: Management, Business, and Digital Insurance Platform.
 *
 * This library provides:
 * - Design system (tokens, theme)
 * - Common UI components (DataTable, SearchBar, StatusBadge, etc.)
 * - Layout components (MainLayout, AuthLayout, etc.)
 * - Form components (FormBuilder, ValidatedInput, etc.)
 * - Utilities (formatting, validation, etc.)
 */

// Re-export from Management Domain shared for consistency
export * from '@management-domain/shared';

// ============================================================================
// FOUNDATION DESIGN SYSTEM
// ============================================================================

export const foundationTheme = {
  palette: {
    primary: {
      main: '#1a237e', // Gogidix Blue
      light: '#534bae',
      dark: '#000051',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#c6a700', // Gold accent
      light: '#ffd633',
      dark: '#8c7500',
      contrastText: '#ffffff',
    },
    tertiary: {
      main: '#00897b', // Teal for success/active states
      light: '#4db6ac',
      dark: '#005b4f',
    },
    error: {
      main: '#d32f2f',
      light: '#ef5350',
      dark: '#b71c1c',
    },
    warning: {
      main: '#ed6c02',
      light: '#ff9800',
      dark: '#e65100',
    },
    info: {
      main: '#0288d1',
      light: '#03a9f4',
      dark: '#01579b',
    },
    success: {
      main: '#2e7d32',
      light: '#4caf50',
      dark: '#1b5e20',
    },
    background: {
      default: '#f5f5f5',
      paper: '#ffffff',
    },
    text: {
      primary: 'rgba(0, 0, 0, 0.87)',
      secondary: 'rgba(0, 0, 0, 0.6)',
      disabled: 'rgba(0, 0, 0, 0.38)',
    },
    divider: 'rgba(0, 0, 0, 0.12)',
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: { fontSize: '2.5rem', fontWeight: 700 },
    h2: { fontSize: '2rem', fontWeight: 600 },
    h3: { fontSize: '1.75rem', fontWeight: 600 },
    h4: { fontSize: '1.5rem', fontWeight: 600 },
    h5: { fontSize: '1.25rem', fontWeight: 500 },
    h6: { fontSize: '1rem', fontWeight: 500 },
    body1: { fontSize: '1rem' },
    body2: { fontSize: '0.875rem' },
    button: { fontSize: '0.875rem', fontWeight: 500, textTransform: 'none' as const },
    caption: { fontSize: '0.75rem' },
  },
  shape: {
    borderRadius: 8,
  },
  spacing: 8,
  shadows: [
    'none',
    '0 1px 3px rgba(0,0,0,0.12), 0 1px 2px rgba(0,0,0,0.24)',
    '0 3px 6px rgba(0,0,0,0.16), 0 3px 6px rgba(0,0,0,0.23)',
    '0 10px 20px rgba(0,0,0,0.19), 0 6px 6px rgba(0,0,0,0.23)',
    '0 14px 28px rgba(0,0,0,0.25), 0 10px 10px rgba(0,0,0,0.22)',
    '0 19px 38px rgba(0,0,0,0.30), 0 15px 12px rgba(0,0,0,0.22)',
  ],
  transitions: {
    duration: {
      shortest: 150,
      shorter: 200,
      short: 250,
      standard: 300,
      complex: 375,
      enteringScreen: 225,
      leavingScreen: 195,
    },
    easing: {
      easeInOut: 'cubic-bezier(0.4, 0, 0.2, 1)',
      easeOut: 'cubic-bezier(0.0, 0, 0.2, 1)',
      easeIn: 'cubic-bezier(0.4, 0, 1, 1)',
      sharp: 'cubic-bezier(0.4, 0, 0.6, 1)',
    },
  },
  zIndex: {
    mobileStepper: 1000,
    fab: 1050,
    speedDial: 1050,
    appBar: 1100,
    drawer: 1200,
    modal: 1300,
    snackbar: 1400,
    tooltip: 1500,
  },
} as const;

// ============================================================================
// DEPARTMENT CONFIGURATIONS
// ============================================================================

export interface DepartmentConfig {
  name: string;
  code: string;
  port: number;
  primaryColor?: string;
  icon?: string;
}

export const DEPARTMENT_CONFIGS: Record<string, DepartmentConfig> = {
  // Management Domain
  'country-admin': { name: 'Country Admin', code: 'CA', port: 8501 },
  'customer-support': { name: 'Customer Support', code: 'CS', port: 8502 },
  'executive-command': { name: 'Executive Command', code: 'EC', port: 8503 },
  'finance-settlement': { name: 'Finance Settlement', code: 'FS', port: 8504 },
  'global-admin': { name: 'Global Admin', code: 'GA', port: 8505 },
  'hr': { name: 'Human Resources', code: 'HR', port: 8506 },
  'pricing-policy': { name: 'Pricing Policy', code: 'PP', port: 8507 },
  'sales': { name: 'Sales', code: 'S', port: 8508 },
  'shared-services': { name: 'Shared Services', code: 'SS', port: 8509 },
  'digital-marketing': { name: 'Digital Marketing', code: 'DM', port: 8510 },
  'compliance-risk': { name: 'Compliance & Risk', code: 'CR', port: 8511 },

  // Business Domain
  'mechanics': { name: 'Mechanics', code: 'ME', port: 8520 },
  'partners-towing': { name: 'Partners Towing', code: 'PT', port: 8521 },
  'vendors-ecommerce': { name: 'Vendors Ecommerce', code: 'VE', port: 8522 },
  'central-monitoring': { name: 'Central Monitoring', code: 'CM', port: 8523 },
  'individual-insurance-customer': { name: 'Individual Insurance', code: 'II', port: 8524 },
  'corporate-insurance-customer': { name: 'Corporate Insurance', code: 'CI', port: 8525 },

  // Insurance Domain
  'business-applications': { name: 'Business Applications', code: 'BA', port: 8601 },
  'claims-assistance': { name: 'Claims Assistance', code: 'CL', port: 8602 },
  'compliance-risk-insurance': { name: 'Compliance Risk', code: 'CR', port: 8603 },
  'customer-distribution': { name: 'Customer Distribution', code: 'CD', port: 8604 },
  'finance-settlement-insurance': { name: 'Finance Settlement', code: 'FS', port: 8605 },
  'insurance-core': { name: 'Insurance Core', code: 'IC', port: 8606 },
  'operations-insurance': { name: 'Operations', code: 'OP', port: 8607 },
  'pricing-underwriting': { name: 'Pricing Underwriting', code: 'PU', port: 8608 },
};

// ============================================================================
// API GATEWAY CONFIGURATION
// ============================================================================

export const API_GATEWAY_CONFIG = {
  baseUrl: process.env.VITE_API_GATEWAY_URL || 'https://api.gogidix.com',
  foundationPort: 8080,
  routes: {
    foundation: '/api/v1/foundation',
    management: '/api/v1/management',
    business: '/api/v1/business',
    insurance: '/api/v1/insurance',
  },
} as const;

// ============================================================================
// ENVIRONMENT VARIABLES
// ============================================================================

export const ENV = {
  get API_GATEWAY_URL() { return import.meta.env.VITE_API_GATEWAY_URL || 'https://api.gogidix.com'; },
  get DEPARTMENT_CODE() { return import.meta.env.VITE_DEPARTMENT_CODE; },
  get AUTH_DOMAIN() { return import.meta.env.VITE_AUTH_DOMAIN || 'auth.gogidix.com'; },
  get SENTRY_DSN() { return import.meta.env.VITE_SENTRY_DSN; },
  get ENVIRONMENT() { return import.meta.env.VITE_ENVIRONMENT || 'development'; },
  get ENABLE_DEBUG() { return import.meta.env.VITE_ENABLE_DEBUG === 'true'; },
} as const;

// ============================================================================
// EXPORT ALL
// ============================================================================

export default {
  theme: foundationTheme,
  departmentConfigs: DEPARTMENT_CONFIGS,
  apiGateway: API_GATEWAY_CONFIG,
  env: ENV,
};
