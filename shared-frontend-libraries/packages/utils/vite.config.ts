import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [react(), dts()],
  build: {
    lib: {
      entry: './src/index.ts',
      name: 'Utils',
      fileName: 'index',
      formats: ['es'],
    },
    rollupOptions: {
      external: ['react', 'react-dom', '@mui/material', '@mui/icons-material', '@shared-frontend-libraries/design-system', '@shared-frontend-libraries/components', '@shared-frontend-libraries/forms', '@shared-frontend-libraries/layouts', '@shared-frontend-libraries/data-display', '@shared-frontend-libraries/dashboards', '@shared-frontend-libraries/brand-assets', '@shared-frontend-libraries/real-time'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
        },
      },
    },
  },
});
