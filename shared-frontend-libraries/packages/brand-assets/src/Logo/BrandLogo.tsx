import React from 'react';
import { Typography, Link } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface BrandLogoProps {
  variant?: 'full' | 'icon' | 'text';
  href?: string;
  size?: 'small' | 'medium' | 'large';
}

/**
 * BrandLogo Component
 * Enterprise-grade brand logo
 */
export const BrandLogo: React.FC<BrandLogoProps> = ({
  variant = 'full',
  href = '/',
  size = 'medium',
}) => {
  const sizeStyles = {
    small: { fontSize: '1.25rem', fontWeight: 700 },
    medium: { fontSize: '1.5rem', fontWeight: 700 },
    large: { fontSize: '2rem', fontWeight: 700 },
  };

  return (
    <Link href={href} sx={{ textDecoration: 'none' }}>
      <Typography
        variant={variant === 'full' ? 'h3' : variant === 'icon' ? 'h6' : 'body1'}
        sx={{
          color: MANAGEMENT_COLORS.primary,
          fontWeight: 700,
          fontSize: sizeStyles[size].fontSize,
          letterSpacing: '-0.5px',
        }}
      >
        RapidAssist
      </Typography>
    </Link>
  );
};
