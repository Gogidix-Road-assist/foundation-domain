import React from 'react';
import { Box, BoxProps } from '@mui/material';

export interface ContentAreaProps extends BoxProps {
  scrollable?: boolean;
  overflow?: 'auto' | 'hidden' | 'visible' | 'scroll';
}

/**
 * ContentArea Component
 * Enterprise-grade main content area
 */
export const ContentArea: React.FC<ContentAreaProps> = ({
  scrollable = true,
  overflow = 'auto',
  children,
  ...props
}) => {
  return (
    <Box
      component="main"
      sx={{
        flexGrow: 1,
        overflow: scrollable ? overflow : 'visible',
        backgroundColor: '#FFFFFF',
      }}
      {...props}
    >
      {children}
    </Box>
  );
};
