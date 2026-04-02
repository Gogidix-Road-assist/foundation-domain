import React from 'react';
import { motion } from 'framer-motion';
import {
  LinearProgress as MuiLinearProgress,
  CircularProgress as MuiCircularProgress,
  Box,
  Typography,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type ProgressColor = 'primary' | 'secondary' | 'success' | 'warning' | 'error';
export type ProgressVariant = 'determinate' | 'indeterminate' | 'buffer';
export type ProgressSize = 'small' | 'medium' | 'large';

export interface ProgressProps {
  value?: number;
  max?: number;
  variant?: ProgressVariant;
  color?: ProgressColor;
  size?: ProgressSize;
  label?: string;
  showValue?: boolean;
  className?: string;
}

export interface CircularProgressProps {
  value?: number;
  size?: number;
  color?: ProgressColor;
  thickness?: number;
  showValue?: boolean;
}

const colorMap: Record<ProgressColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  success: MANAGEMENT_COLORS.success,
  warning: MANAGEMENT_COLORS.warning,
  error: MANAGEMENT_COLORS.error,
};

const thicknessMap: Record<ProgressSize, number> = {
  small: 4,
  medium: 6,
  large: 8,
};

/**
 * Progress Component
 * Enterprise-grade linear progress bar
 */
export const Progress = React.forwardRef<HTMLDivElement, ProgressProps>(
  (
    {
      value = 0,
      max = 100,
      variant = 'determinate',
      color = 'primary',
      size = 'medium',
      label,
      showValue = false,
      className,
    },
    ref
  ) => {
    const normalizedValue = Math.min(Math.max(value, 0), max);
    const percentage = Math.round((normalizedValue / max) * 100);

    return (
      <Box ref={ref} className={className} sx={{ width: '100%' }}>
        {(label || showValue) && (
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            {label && (
              <Typography variant="body2" sx={{ color: '#374151', fontWeight: 500 }}>
                {label}
              </Typography>
            )}
            {showValue && (
              <Typography variant="body2" sx={{ color: '#6B7280' }}>
                {percentage}%
              </Typography>
            )}
          </Box>
        )}
        <MuiLinearProgress
          value={normalizedValue}
          valueBuffer={variant === 'buffer' ? percentage + 10 : undefined}
          variant={variant}
          sx={{
            height: thicknessMap[size],
            borderRadius: size === 'large' ? '8px' : '4px',
            backgroundColor: '#F3F4F6',
            '& .MuiLinearProgress-bar': {
              backgroundColor: colorMap[color],
              borderRadius: size === 'large' ? '8px' : '4px',
            },
            '& .MuiLinearProgress-bar1Buffer': {
              backgroundColor: `${colorMap[color]}40`,
            },
            '& .MuiLinearProgress-bar2Buffer': {
              backgroundColor: colorMap[color],
            },
          }}
        />
      </Box>
    );
  }
);

Progress.displayName = 'Progress';

/**
 * CircularProgress Component
 * Circular progress indicator
 */
export const CircularProgress: React.FC<CircularProgressProps> = ({
  value,
  size = 40,
  color = 'primary',
  thickness = 4,
  showValue = false,
}) => {
  return (
    <Box sx={{ position: 'relative', display: 'inline-flex' }}>
      <MuiCircularProgress
        variant={value !== undefined ? 'determinate' : 'indeterminate'}
        value={value}
        size={size}
        thickness={thickness}
        sx={{
          color: colorMap[color],
        }}
      />
      {showValue && value !== undefined && (
        <Box
          sx={{
            top: 0,
            left: 0,
            bottom: 0,
            right: 0,
            position: 'absolute',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <Typography variant="caption" sx={{ fontSize: size * 0.25, fontWeight: 600 }}>
            {`${Math.round(value)}%`}
          </Typography>
        </Box>
      )}
    </Box>
  );
};

/**
 * ProgressSteps Component
 * Step progress indicator
 */
export interface ProgressStep {
  label: string;
  completed?: boolean;
  active?: boolean;
  error?: boolean;
}

export interface ProgressStepsProps {
  steps: ProgressStep[];
  currentStep?: number;
}

export const ProgressSteps: React.FC<ProgressStepsProps> = ({ steps, currentStep = 0 }) => {
  return (
    <Box sx={{ width: '100%', display: 'flex', alignItems: 'center' }}>
      {steps.map((step, index) => (
        <React.Fragment key={index}>
          <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', flex: 1 }}>
            <Box
              sx={{
                width: '32px',
                height: '32px',
                borderRadius: '50%',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                backgroundColor: step.error
                  ? MANAGEMENT_COLORS.error
                  : step.completed || step.active
                  ? MANAGEMENT_COLORS.primary
                  : '#E5E7EB',
                color: step.completed || step.active || step.error ? '#FFFFFF' : '#9CA3AF',
                fontWeight: 600,
                fontSize: '0.875rem',
                transition: 'all 0.3s',
              }}
            >
              {step.error ? '!' : step.completed ? '✓' : index + 1}
            </Box>
            <Typography
              variant="caption"
              sx={{
                mt: 1,
                color: step.active || step.completed ? '#111827' : '#9CA3AF',
                fontWeight: step.active ? 600 : 400,
              }}
            >
              {step.label}
            </Typography>
          </Box>
          {index < steps.length - 1 && (
            <Box
              sx={{
                flex: 2,
                height: '2px',
                mx: 1,
                backgroundColor:
                  index < currentStep
                    ? MANAGEMENT_COLORS.primary
                    : '#E5E7EB',
                transition: 'all 0.3s',
              }}
            />
          )}
        </React.Fragment>
      ))}
    </Box>
  );
};
