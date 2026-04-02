import { LHCICommand, LHCIStore } from '@lhci/cli';

export const upload = {
  target: 'temporary-public-storage',
};

export const collect: {
  url: ['http://localhost:3000'],
  numberOfRuns: 3,
  chromePath: '/Applications/Google Chrome.app/Contents/MacOS/Google Chrome',
};

export const assert = {
  preset: 'lighthouse:recommended',
  assertions: {
    'categories:performance': ['error', { minScore: 0.9 }],
    'categories:accessibility': ['error', { minScore: 0.9 }],
    'categories:best-practices': ['warn', { minScore: 0.9 }],
    'categories:seo': ['warn', { minScore: 0.9 }],
    'categories:pwa': ['off'],
  },
};

export const assertMatrix = {
  preset: 'lighthouse:recommended',
  assertions: {
    // Performance thresholds
    'categories:performance': [
      'off', // Allow lower scores in development
    ],
    'first-contentful-paint': ['warn', { minNumeric: 1500 }],
    'largest-contentful-paint': ['warn', { minNumeric: 2500 }],
    'total-blocking-time': ['error', { maxNumeric: 600 }],
    'cumulative-layout-shift': ['warn', { maxNumeric: 0.1 }],
    'speed-index': ['warn', { maxNumeric: 3400 }],
    'interactive': ['warn', { maxNumeric: 4000 }],
    'first-meaningful-paint': ['warn', { maxNumeric: 2000 }],
    'max-potential-fid': ['warn', { maxNumeric: 800 }],

    // Accessibility thresholds (WCAG AA)
    'categories:accessibility': ['warn', { minScore: 0.95 }],
    'color-contrast': ['error', { maxScore: 0 }],
    'aria-valid-attr-value': ['warn', { minScore: 1 }],
    'button-name': ['warn', { minScore: 0.9 }],
    'link-name': ['warn', { minScore: 0.9 }],
    'bypass': ['warn', { minScore: 1 }],
    'frame-title': ['warn', { minScore: 1 }],
    'image-alt': ['warn', { minScore: 0.9 }],
    'label': ['warn', { minScore: 0.9 }],
    'link-text': ['warn', { minScore: 0.9 }],
    'html-has-lang': ['warn', { minScore: 1 }],
    'valid-lang': ['warn', { minScore: 1 }],
    'listitem': ['warn', { minScore: 0.95 }],
    'doctype-html5': ['warn', { minScore: 1 }],
    'meta-viewport': ['warn', { minScore: 0.95 }],
    'meta-viewport-large': ['warn', { minScore: 1 }],
    'meta-description': ['warn', { minScore: 0.9 }],
  },
};

// CI configuration
export const server = {
  port: 9001,
  storage: {
    storageMethod: 'sql',
    sqlDatabasePath: './lhci.db',
  },
};
