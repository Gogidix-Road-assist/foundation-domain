import React from 'react';
import { Badge as MuiBadge, BadgeProps as MuiBadgeProps } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type BadgeSize = 'small' | 'medium' | 'large';

export interface DataBadgeProps extends Omit<MuiBadgeProps, 'color'> {
  count?: number;
  max?: number;
  showZero?: boolean;
  size?: BadgeSize;
}

/**
 * DataBadge Component
 * Enterprise-grade badge for status indicators
 */
export const DataBadge: React.FC<DataBadgeProps> = ({
  count,
  max = 99,
  showZero = false,
  size = 'medium',
  ...props
}) => {
  const sizeStyles = {
    small: { fontSize: '0.75rem' },
    medium: { fontSize: '0.875rem' },
    large: { fontSize: '1rem' },
  };

  const displayCount = count > max ? `${max}+` : count?.toString();

  return (
    <MuiBadge
      {...props}
      badgeContent={displayCount}
      showZero={showZero}
      color={MANAGEMENT_COLORS.primary}
      sx={{
        '& .MuiBadge-badge': {
          fontSize: sizeStyles[size].fontSize,
          fontWeight: 600,
          backgroundColor: MANAGEMENT_COLORS.primary,
        },
      }}
    />
  );
};
