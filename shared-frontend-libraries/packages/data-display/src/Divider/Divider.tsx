import React from 'react';
import { Divider as MuiDivider, DividerProps as MuiDividerProps } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type DividerVariant = 'full' | 'inset' | 'middle';

export interface DividerProps extends Omit<MuiDividerProps, 'variant'> {
  variant?: DividerVariant;
  color?: 'primary' | 'secondary' | 'error' | 'success';
  label?: string;
}

const colorMap = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  error: MANAGEMENT_COLORS.error,
  success: MANAGEMENT_COLORS.success,
};

/**
 * Divider Component
 * Enterprise-grade divider with color variants
 */
export const Divider: React.FC<DividerProps> = ({
  variant = 'full',
  color,
  label,
  ...props
}) => {
  return (
    <MuiDivider
      {...props}
      variant={variant}
      sx={{
        borderColor: color ? colorMap[color] : '#E5E7EB',
        my: 2,
      }}
    >
      {label}
    </MuiDivider>
  );
};
