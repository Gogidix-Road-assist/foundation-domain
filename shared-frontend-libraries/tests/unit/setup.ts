import '@testing-library/jest-dom';
import { cleanup } from '@testing-library/react';
import { afterEach, vi, beforeAll, afterAll } from 'vitest';

// Cleanup after each test
afterEach(() => {
  cleanup();
});

// Mock IntersectionObserver
global.IntersectionObserver = class IntersectionObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  takeRecords() {
    return [];
  }
  unobserve() {}
  root: any = null;
  rootMargin: any = '';
  thresholds: any[] = [];
} as any;

// Mock ResizeObserver
global.ResizeObserver = class ResizeObserver {
  constructor() {}
  disconnect() {}
  observe() {}
  unobserve() {}
} as any;

// Mock window.matchMedia
Object.defineProperty(window, 'matchMedia', {
  writable: true,
  value: vi.fn().mockImplementation((query) => ({
    matches: false,
    media: query,
    onchange: null,
    addListener: vi.fn(),
    removeListener: vi.fn(),
    addEventListener: vi.fn(),
    removeEventListener: vi.fn(),
    dispatchEvent: vi.fn(),
  })),
});

// Mock console methods to reduce noise in tests
const originalError = console.error;
const originalWarn = console.warn;

beforeAll(() => {
  console.error = (...args: any[]) => {
    if (
      typeof args[0] === 'string' &&
      (args[0].includes('Warning:') ||
       args[0].includes('Not implemented:') ||
       args[0].includes('componentWillReceiveProps'))
    ) {
      return;
    }
    originalError.call(console, ...args);
  };

  console.warn = (...args: any[]) => {
    if (
      typeof args[0] === 'string' &&
      args[0].includes('componentWillReceiveProps')
    ) {
      return;
    }
    originalWarn.call(console, ...args);
  };
});

afterAll(() => {
  console.error = originalError;
  console.warn = originalWarn;
});

// Mock ScrollToBehavior
window.scrollTo = vi.fn();
window.scrollBy = vi.fn();
window.scrollIntoView = vi.fn();

// Mock localStorage
const localStorageMock = (() => {
  let store: Record<string, string> = {};
  return {
    getItem: (key: string) => store[key] || null,
    setItem: (key: string, value: string) => {
      store[key] = value.toString();
    },
    removeItem: (key: string) => {
      delete store[key];
    },
    clear: () => {
      store = {};
    },
    get length() {
      return Object.keys(store).length;
    },
    key: (index: number) => {
      return Object.keys(store)[index] || null;
    },
  };
})();

Object.defineProperty(window, 'localStorage', {
  value: localStorageMock,
  writable: true,
});

// Mock sessionStorage
Object.defineProperty(window, 'sessionStorage', {
  value: localStorageMock,
  writable: true,
});

// Mock requestAnimationFrame
global.requestAnimationFrame = (callback: FrameRequestCallback) => {
  return setTimeout(callback, 16) as unknown as number;
};

global.cancelAnimationFrame = (id: number) => {
  clearTimeout(id);
};

// Mock getComputedStyle
global.getComputedStyle = vi.fn(() => ({
  getPropertyValue: (prop: string) => {
    if (prop === 'display') return 'block';
    if (prop === 'position') return 'static';
    if (prop === 'width') return 'auto';
    return '';
  },
}) as any);

// Export test utilities
export const createMockRouter = () => ({
  push: vi.fn(),
  replace: vi.fn(),
  pathname: '/',
  query: {},
  asPath: '/',
  route: '/',
});

export const createMockTheme = () => ({
  palette: {
    primary: { main: '#3B82F6', light: '#60A5FA', dark: '#2563EB' },
    secondary: { main: '#6B7280', light: '#9CA3AF', dark: '#4B5563' },
    error: { main: '#EF4444' },
    warning: { main: '#F59E0B' },
    success: { main: '#10B981' },
    info: { main: '#3B82F6' },
    text: { primary: '#1F2937', secondary: '#6B7280' },
    background: { paper: '#FFFFFF', default: '#F9FAFB' },
    divider: '#E5E7EB',
  },
  typography: {
    fontFamily: 'Inter, sans-serif',
    fontSize: 14,
    fontWeightRegular: 400,
    fontWeightMedium: 500,
    fontWeightBold: 600,
  },
  spacing: (factor: number) => `${8 * factor}px`,
  breakpoints: {
    xs: 0,
    sm: 600,
    md: 900,
    lg: 1200,
    xl: 1536,
  },
  shadows: {
    1: '0 1px 3px rgba(0,0,0,0.12)',
    2: '0 1px 5px rgba(0,0,0,0.2)',
    3: '0 5px 15px rgba(0,0,0,0.17)',
  },
  zIndex: {
    mobileStepper: 1000,
    speedDial: 1050,
    appBar: 1100,
    drawer: 1200,
    modal: 1300,
    snackbar: 1400,
    tooltip: 1500,
  },
  direction: 'ltr',
});

export const mockEvent = (type: string) => ({
  type,
  target: {
    value: '',
    checked: false,
  },
  currentTarget: {
    value: '',
    checked: false,
  },
  preventDefault: vi.fn(),
  stopPropagation: vi.fn(),
});

export const waitForComponentToPaint = async () => {
  await new Promise(resolve => requestAnimationFrame(() => requestAnimationFrame(resolve)));
};

export const createMockMutationObserver = () => ({
  observe: vi.fn(),
  disconnect: vi.fn(),
  takeRecords: vi.fn(() => []),
});

// Mock Leaflet (if used in tests)
vi.mock('leaflet', () => ({
  default: {
    map: vi.fn(() => ({
      setView: vi.fn(),
      fitBounds: vi.fn(),
      on: vi.fn(),
      remove: vi.fn(),
    })),
    tileLayer: vi.fn(() => ({
      addTo: vi.fn(() => ({})),
    })),
    marker: vi.fn(() => ({
      addTo: vi.fn(() => ({})),
      bindPopup: vi.fn(() => ({})),
      setIcon: vi.fn(),
      on: vi.fn(() => ({})),
      remove: vi.fn(() => ({})),
    })),
    latLngBounds: vi.fn(() => ({})),
    divIcon: vi.fn(() => ({})),
    Icon: {
      Default: {
        mergeOptions: vi.fn(),
      },
      prototype: {
        _getIconUrl: vi.fn(),
      },
    },
  },
}));

vi.mock('leaflet/dist/leaflet.css', () => ({}));
