import React from 'react';
import { motion } from 'framer-motion';
import {
  Avatar as MuiAvatar,
  AvatarProps as MuiAvatarProps,
  AvatarGroup as MuiAvatarGroup,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type AvatarSize = 'small' | 'medium' | 'large' | 'xl';
export type AvatarColor = 'primary' | 'secondary' | 'success' | 'warning' | 'error' | 'info';

export interface AvatarProps extends Omit<MuiAvatarProps, 'size'> {
  size?: AvatarSize;
  color?: AvatarColor;
  src?: string;
  alt?: string;
  fallbackText?: string;
}

export interface AvatarGroupProps {
  max?: number;
  size?: AvatarSize;
  children: React.ReactNode;
  total?: number;
}

const sizeMap: Record<AvatarSize, { width: number; height: number; fontSize: string }> = {
  small: { width: 24, height: 24, fontSize: '0.625rem' },
  medium: { width: 40, height: 40, fontSize: '0.875rem' },
  large: { width: 56, height: 56, fontSize: '1.25rem' },
  xl: { width: 80, height: 80, fontSize: '1.75rem' },
};

const colorMap: Record<AvatarColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  success: MANAGEMENT_COLORS.success,
  warning: MANAGEMENT_COLORS.warning,
  error: MANAGEMENT_COLORS.error,
  info: MANAGEMENT_COLORS.info,
};

/**
 * Avatar Component
 * Enterprise-grade avatar with fallback text and color variants
 */
export const Avatar = React.forwardRef<HTMLDivElement, AvatarProps>(
  (
    {
      size = 'medium',
      color = 'primary',
      src,
      alt = 'Avatar',
      fallbackText,
      className,
      children,
      ...props
    },
    ref
  ) => {
    const sizeConfig = sizeMap[size];

    return (
      <MuiAvatar
        ref={ref}
        src={src}
        alt={alt}
        className={className}
        sx={{
          width: sizeConfig.width,
          height: sizeConfig.height,
          fontSize: sizeConfig.fontSize,
          backgroundColor: !src ? colorMap[color] : undefined,
          color: '#FFFFFF',
          fontWeight: 500,
        }}
        {...props}
      >
        {children || fallbackText || alt.charAt(0).toUpperCase()}
      </MuiAvatar>
    );
  }
);

Avatar.displayName = 'Avatar';

/**
 * AvatarGroup Component
 * Group of avatars with overlap
 */
export const AvatarGroup: React.FC<AvatarGroupProps> = ({
  max = 5,
  size = 'medium',
  children,
  total,
}) => {
  return (
    <MuiAvatarGroup
      max={max}
      total={total}
      sx={{
        '& .MuiAvatar-root': {
          width: sizeMap[size].width,
          height: sizeMap[size].height,
          fontSize: sizeMap[size].fontSize,
          border: '2px solid #FFFFFF',
        },
      }}
    >
      {children}
    </MuiAvatarGroup>
  );
};
