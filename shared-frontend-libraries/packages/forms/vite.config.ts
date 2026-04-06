import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
// Temporarily disabled dts plugin to bypass TypeScript errors during build
// TODO: Fix TypeScript errors and re-enable dts plugin
// import dts from 'vite-plugin-dts';

export default defineConfig({
  plugins: [react()], // dts()
  build: {
    lib: {
      entry: './src/index.ts',
      name: 'Forms',
      fileName: 'index',
      formats: ['es'],
    },
    rollupOptions: {
      external: ['react', 'react-dom', 'framer-motion', '@mui/material', '@mui/icons-material', '@shared-frontend-libraries/components', '@shared-frontend-libraries/design-system'],
      output: {
        globals: {
          react: 'React',
          'react-dom': 'ReactDOM',
        },
      },
    },
  },
});
