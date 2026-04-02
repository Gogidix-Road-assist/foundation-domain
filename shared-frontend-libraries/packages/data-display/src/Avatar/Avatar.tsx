import React from 'react';
import { Avatar as MuiAvatar, AvatarProps as MuiAvatarProps } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type AvatarSize = 'small' | 'medium' | 'large' | 'xlarge';
export type AvatarVariant = 'circle' | 'square';

export interface DataAvatarProps extends Omit<MuiAvatarProps, 'src'> {
  src?: string;
  alt?: string;
  name?: string;
  fallbackText?: string;
  size?: AvatarSize;
  variant?: AvatarVariant;
  showBorder?: boolean;
  onlineStatus?: 'online' | 'offline' | 'away' | 'busy';
}

const sizeMap: Record<AvatarSize, { width: number; height: number; fontSize: string }> = {
  small: { width: 32, height: 32, fontSize: '0.75rem' },
  medium: { width: 40, height: 40, fontSize: '0.875rem' },
  large: { width: 56, height: 56, fontSize: '1.125rem' },
  xlarge: { width: 80, height: 80, fontSize: '1.5rem' },
};

const statusColors = {
  online: '#10B981',
  offline: '#6B7280',
  away: '#F59E0B',
  busy: '#EF4444',
};

/**
 * DataAvatar Component
 * Enterprise-grade avatar with online status and fallback text
 */
export const DataAvatar: React.FC<DataAvatarProps> = ({
  src,
  alt,
  name,
  fallbackText,
  size = 'medium',
  variant = 'circle',
  showBorder = true,
  onlineStatus = 'online',
  ...props
}) => {
  const sizeConfig = sizeMap[size];

  return (
    <Box sx={{ position: 'relative', display: 'inline-flex' }}>
      <MuiAvatar
        {...props}
        src={src}
        alt={alt}
        sx={{
          width: sizeConfig.width,
          height: sizeConfig.height,
          fontSize: sizeConfig.fontSize,
          backgroundColor: '#E5E7EB',
          border: showBorder ? `2px solid ${statusColors[onlineStatus]}` : 'none',
        }}
      >
        {fallbackText && !src && name?.charAt(0)}
      </MuiAvatar>
      {onlineStatus !== 'offline' && (
        <Box
          sx={{
            position: 'absolute',
            bottom: 2,
            right: 2,
            width: 12,
            height: 12,
            borderRadius: '50%',
            backgroundColor: statusColors[onlineStatus],
            border: '2px solid #FFFFFF',
          }}
        />
      )}
    </Box>
  );
};
