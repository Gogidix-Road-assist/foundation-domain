import { defineConfig } from 'vitest/config';
import react from '@vitejs/plugin-react';
import path from 'path';

export default defineConfig({
  plugins: [react()],
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./tests/unit/setup.ts'],
    include: ['**/*.{test,spec}.{ts,tsx}'],
    exclude: [
      'node_modules',
      'dist',
      'build',
      '**/*.stories.{ts,tsx}',
      '**/*.config.{ts,js}',
    ],
    coverage: {
      provider: 'v8',
      reporter: ['text', 'json', 'html', 'lcov', 'text-summary'],
      exclude: [
        'node_modules/',
        'tests/',
        '**/*.test.{ts,tsx}',
        '**/*.test.{ts,tsx}',
        '**/*.spec.{ts,tsx}',
        '**/*.stories.{ts,tsx}',
        '**/dist/**',
        '**/build/**',
        '**/*.config.{ts,js}',
        '**/index.ts',
        '**/index.d.ts',
        '**/*.mock.{ts,tsx}',
        '**/types/**',
      ],
      // Coverage thresholds for all packages
      statements: 80,
      branches: 75,
      functions: 80,
      lines: 80,
      // Per-file thresholds
      perFile: true,
      // All files must meet at least 50% coverage
      thresholds: {
        lines: 50,
        functions: 50,
        branches: 50,
        statements: 50,
      },
      // Output directory
      reportsDirectory: './coverage',
    },
    // Test timeout
    testTimeout: 10000,
    // Hook timeout
    hookTimeout: 10000,
    // Teardown timeout
    teardownTimeout: 10000,
    // Isolate tests
    isolate: true,
    // Clear mocks between tests
    clearMocks: true,
    // Restore mocks after each test
    restoreMocks: true,
  },
  resolve: {
    alias: {
      '@shared-frontend-libraries/design-system': path.resolve(__dirname, './packages/design-system/src'),
      '@shared-frontend-libraries/components': path.resolve(__dirname, './packages/components/src'),
      '@shared-frontend-libraries/layouts': path.resolve(__dirname, './packages/layouts/src'),
      '@shared-frontend-libraries/forms': path.resolve(__dirname, './packages/forms/src'),
      '@shared-frontend-libraries/data-display': path.resolve(__dirname, './packages/data-display/src'),
      '@shared-frontend-libraries/real-time': path.resolve(__dirname, './packages/real-time/src'),
      '@shared-frontend-libraries/dashboards': path.resolve(__dirname, './packages/dashboards/src'),
      '@shared-frontend-libraries/brand-assets': path.resolve(__dirname, './packages/brand-assets/src'),
      '@shared-frontend-libraries/utils': path.resolve(__dirname, './packages/utils/src'),
    },
  },
});
