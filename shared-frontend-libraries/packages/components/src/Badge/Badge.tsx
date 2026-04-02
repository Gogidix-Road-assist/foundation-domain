import React from 'react';
import { motion } from 'framer-motion';
import {
  Badge as MuiBadge,
  BadgeProps as MuiBadgeProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type BadgeColor = 'primary' | 'secondary' | 'error' | 'success' | 'warning';

export interface BadgeProps extends Omit<MuiBadgeProps, 'color'> {
  color?: BadgeColor;
  count?: number;
  dot?: boolean;
  showZero?: boolean;
  max?: number;
}

const colorMap: Record<BadgeColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  error: MANAGEMENT_COLORS.error,
  success: MANAGEMENT_COLORS.success,
  warning: MANAGEMENT_COLORS.warning,
};

/**
 * Badge Component
 * Enterprise-grade badge for notifications and status indicators
 */
export const Badge = React.forwardRef<HTMLSpanElement, BadgeProps>(
  (
    {
      color = 'primary',
      count,
      dot = false,
      showZero = false,
      max = 99,
      children,
      className,
      ...props
    },
    ref
  ) => {
    return (
      <MuiBadge
        ref={ref}
        badgeContent={dot ? undefined : count}
        invisible={!dot && count === 0 && !showZero}
        max={max}
        color={color as any}
        className={className}
        sx={{
          '& .MuiBadge-badge': {
            backgroundColor: dot ? colorMap[color] : undefined,
            color: '#FFFFFF',
            fontWeight: 600,
            fontSize: '0.75rem',
          },
        }}
        {...props}
      >
        {children}
      </MuiBadge>
    );
  }
);

Badge.displayName = 'Badge';
