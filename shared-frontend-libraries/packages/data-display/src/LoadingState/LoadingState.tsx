import React from 'react';
import { Box, CircularProgress, Typography } from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type LoadingSize = 'small' | 'medium' | 'large';

export interface LoadingStateProps {
  message?: string;
  size?: LoadingSize;
  progress?: number;
}

const sizeMap = {
  small: { width: 40, height: 40 },
  medium: { width: 64, height: 64 },
  large: { width: 80, height: 80 },
};

/**
 * LoadingState Component
 * Enterprise-grade loading indicator
 */
export const LoadingState: React.FC<LoadingStateProps> = ({
  message = 'Loading...',
  size = 'medium',
  progress,
}) => {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '300px',
        padding: '48px',
        backgroundColor: '#FFFFFF',
        borderRadius: 2,
      }}
    >
      <CircularProgress
        {...sizeMap[size]}
        sx={{
          color: MANAGEMENT_COLORS.primary,
        }}
      />
      <Box sx={{ mt: 3, textAlign: 'center' }}>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mb: 1 }}
        >
          {message}
        </Typography>
        {progress !== undefined && (
          <Typography variant="caption" color="text.secondary">
            {Math.round(progress)}% complete
          </Typography>
        )}
      </Box>
    </Box>
  );
};
