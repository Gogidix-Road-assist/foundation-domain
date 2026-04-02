import React from 'react';
import { Box, Typography, Button } from '@mui/material';
import { Search, Description } from '@mui/icons-material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type EmptyStateVariant = 'no-data' | 'no-results' | 'no-matches' | 'error' | 'success';

export interface EmptyStateProps {
  variant?: EmptyStateVariant;
  title?: string;
  description?: string;
  action?: {
    label: string;
    onClick: () => void;
  };
}

const variantConfig = {
  'no-data': {
    icon: <Search fontSize="large" />,
    title: 'No Data Available',
    description: 'There are no items to display at this time.',
  },
  'no-results': {
    icon: <Description fontSize="large" />,
    title: 'No Results Found',
    description: 'We couldn\'t find any results matching your search.',
  },
  'no-matches': {
    icon: <Search fontSize="large" />,
    title: 'No Matches Found',
    description: 'No items match the current filters.',
  },
  error: {
    icon: <Error fontSize="large" sx={{ color: MANAGEMENT_COLORS.error }} />,
    title: 'Something Went Wrong',
    description: 'An error occurred while loading data. Please try again.',
  },
  success: {
    icon: <CheckCircle fontSize="large" sx={{ color: MANAGEMENT_COLORS.success }} />,
    title: 'Operation Successful',
    description: 'Your changes have been saved successfully.',
  },
};

/**
 * EmptyState Component
 * Enterprise-grade empty state with configurable variants
 */
export const EmptyState: React.FC<EmptyStateProps> = ({
  variant = 'no-data',
  title,
  description,
  action,
}) => {
  const config = variantConfig[variant];

  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        minHeight: '400px',
        padding: '48px 24px',
        textAlign: 'center',
        backgroundColor: '#FFFFFF',
        borderRadius: 2,
      }}
    >
      <Box
        sx={{
          width: 80,
          height: 80,
          borderRadius: '50%',
          backgroundColor: '#F3F4F6',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          mb: 3,
        }}
      >
        {config.icon}
      </Box>
      <Typography
        variant="h6"
        sx={{
          fontWeight: 600,
          color: '#111827',
          mb: 1,
        }}
      >
        {title || config.title}
      </Typography>
      <Typography
        variant="body2"
        color="text.secondary"
        sx={{
          maxWidth: 400,
          mb: 3,
        }}
      >
        {description || config.description}
      </Typography>
      {action && (
        <Button
          variant="primary"
          onClick={action.onClick}
          sx={{ mt: 2 }}
        >
          {action.label}
        </Button>
      )}
    </Box>
  );
};
