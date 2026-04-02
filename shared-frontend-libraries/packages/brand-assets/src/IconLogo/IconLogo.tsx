import React from 'react';
import { Typography, Box } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface IconLogoProps {
  variant?: 'circle' | 'square';
  size?: 'small' | 'medium' | 'large';
}

/**
 * IconLogo Component
 * Enterprise-grade icon logo
 */
export const IconLogo: React.FC<IconLogoProps> = ({
  variant = 'circle',
  size = 'medium',
}) => {
  const sizeStyles = {
    small: { width: 40, height: 40 },
    medium: { width: 48, height: 48 },
    large: { width: 64, height: 64 },
  };

  return (
    <Box
      sx={{
        width: sizeStyles[size].width,
        height: sizeStyles[size].height,
        borderRadius: variant === 'circle' ? '50%' : '12%',
        backgroundColor: MANAGEMENT_COLORS.primary,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
      }}
    >
      <Typography
        variant="h4"
        sx={{
          color: '#FFFFFF',
          fontWeight: 700,
          fontSize: '1.25rem',
        }}
      >
        RA
      </Typography>
    </Box>
  );
};
