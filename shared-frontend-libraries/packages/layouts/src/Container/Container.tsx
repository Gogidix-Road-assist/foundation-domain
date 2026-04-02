import React from 'react';
import { Box, BoxProps } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ContainerSize = 'small' | 'medium' | 'large' | 'full';

export interface ContainerProps extends Omit<BoxProps, 'maxWidth'> {
  size?: ContainerSize;
  centered?: boolean;
  noPadding?: boolean;
}

const maxWidthMap: Record<ContainerSize, { maxWidth: string; padding: string }> = {
  small: { maxWidth: '640px', padding: '16px' },
  medium: { maxWidth: '1024px', padding: '24px' },
  large: { maxWidth: '1280px', padding: '32px' },
  full: { maxWidth: '100%', padding: '24px' },
};

/**
 * Container Component
 * Enterprise-grade responsive container
 */
export const Container: React.FC<ContainerProps> = ({
  size = 'medium',
  centered = false,
  noPadding = false,
  children,
  ...props
}) => {
  const config = maxWidthMap[size];

  return (
    <Box
      sx={{
        maxWidth: config.maxWidth,
        margin: centered ? '0 auto' : undefined,
        padding: noPadding ? undefined : config.padding,
        backgroundColor: '#FFFFFF',
        minHeight: '100vh',
      }}
      {...props}
    >
      {children}
    </Box>
  );
};
