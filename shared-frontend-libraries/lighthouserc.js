module.exports = {
  assert: {
    preset: 'lighthouse:recommended',
    assertions: {
      'categories:performance': ['error', { minScore: 0.9 }],
      'categories:accessibility': ['error', { minScore: 0.95 }],
      'categories:best-practices': ['error', { minScore: 0.9 }],
      'categories:seo': ['error', { minScore: 0.9 }],
      'first-contentful-paint': ['error', { maxNumericValue: 1500 }],
      'interactive': ['error', { maxNumericValue: 3000 }],
      'largest-contentful-paint': ['error', { maxNumericValue: 2500 }],
      'cumulative-layout-shift': ['error', { maxNumericValue: 0.1 }],
    },
  },
  collect: {
    url: ['http://localhost:3000'],
    numberOfRuns: 3,
    staticDistDir: './dist',
  },
  upload: {
    target: 'temporary-public-storage',
  },
};
