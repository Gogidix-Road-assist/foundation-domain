import React from 'react';
import { Box, BoxProps } from '@mui/material';

export type LayoutType = 'default' | 'minimal' | 'fullscreen' | 'sidebar';

export interface LayoutProps extends BoxProps {
  type?: LayoutType;
  headerHeight?: number;
  footerHeight?: number;
  sidebarWidth?: number;
}

/**
 * Layout Component
 * Enterprise-grade layout wrapper
 */
export const Layout: React.FC<LayoutProps> = ({
  type = 'default',
  headerHeight = 64,
  footerHeight = 48,
  sidebarWidth = 250,
  children,
  ...props
}) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        minHeight: '100vh',
        backgroundColor: '#F9FAFB',
      }}
      {...props}
    >
      {children}
    </Box>
  );
};
