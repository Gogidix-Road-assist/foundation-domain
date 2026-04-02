import React from 'react';
import {
  Box,
  Typography,
  Chip,
  useTheme,
  alpha,
  SxProps,
} from '@mui/material';
import { motion } from 'framer-motion';
import TrendingUpIcon from '@mui/icons-material/TrendingUp';
import TrendingDownIcon from '@mui/icons-material/TrendingDown';
import TrendingFlatIcon from '@mui/icons-material/TrendingFlat';
import ArrowUpwardIcon from '@mui/icons-material/ArrowUpward';
import ArrowDownwardIcon from '@mui/icons-material/ArrowDownward';
import RemoveIcon from '@mui/icons-material/Remove';

export type TrendDirection = 'up' | 'down' | 'flat' | 'auto';

export interface TrendIndicatorProps {
  value: number;
  label?: string;
  previousValue?: number;
  direction?: TrendDirection;
  showIcon?: boolean;
  showPercentage?: boolean;
  color?: 'auto' | 'success' | 'error' | 'neutral';
  size?: 'small' | 'medium' | 'large';
  variant?: 'default' | 'chip' | 'compact';
  sx?: SxProps;
  className?: string;
  precision?: number;
  format?: (value: number) => string;
}

const ICON_SIZE_MAP = {
  small: 16,
  medium: 20,
  large: 24,
};

const TYPOGRAPHY_VARIANT_MAP = {
  small: 'caption' as const,
  medium: 'body2' as const,
  large: 'body1' as const,
};

export function TrendIndicator({
  value,
  label,
  previousValue,
  direction = 'auto',
  showIcon = true,
  showPercentage = true,
  color = 'auto',
  size = 'medium',
  variant = 'default',
  sx,
  className,
  precision = 1,
  format,
}: TrendIndicatorProps) {
  const theme = useTheme();

  // Calculate trend direction if auto
  const calculatedDirection: 'up' | 'down' | 'flat' =
    direction === 'auto'
      ? value > 0
        ? 'up'
        : value < 0
        ? 'down'
        : 'flat'
      : direction;

  // Calculate percentage if previousValue is provided
  const percentage = previousValue
    ? ((value - previousValue) / Math.abs(previousValue)) * 100
    : null;

  // Determine color based on direction and color prop
  const getColor = () => {
    if (color !== 'auto') return color;

    // Auto color: up = success (good increase), down = error (bad decrease)
    // But this might need customization based on context
    if (calculatedDirection === 'up') return 'success';
    if (calculatedDirection === 'down') return 'error';
    return 'neutral';
  };

  const trendColor = getColor();

  // Get icon based on direction
  const getIcon = () => {
    if (!showIcon) return null;

    const iconProps = { style: { fontSize: ICON_SIZE_MAP[size] } };

    switch (calculatedDirection) {
      case 'up':
        return <TrendingUpIcon {...iconProps} />;
      case 'down':
        return <TrendingDownIcon {...iconProps} />;
      case 'flat':
        return <TrendingFlatIcon {...iconProps} />;
    }
  };

  const getCompactIcon = () => {
    if (!showIcon) return null;

    const iconProps = { style: { fontSize: ICON_SIZE_MAP[size] } };

    switch (calculatedDirection) {
      case 'up':
        return <ArrowUpwardIcon {...iconProps} />;
      case 'down':
        return <ArrowDownwardIcon {...iconProps} />;
      case 'flat':
        return <RemoveIcon {...iconProps} />;
    }
  };

  // Get background color for chip variant
  const getChipColor = () => {
    const colors = {
      success: alpha(theme.palette.success.main, 0.1),
      error: alpha(theme.palette.error.main, 0.1),
      neutral: alpha(theme.palette.grey[500], 0.1),
    };
    return colors[trendColor];
  };

  const getTextColor = () => {
    const colors = {
      success: theme.palette.success.main,
      error: theme.palette.error.main,
      neutral: theme.palette.grey[700],
    };
    return colors[trendColor];
  };

  // Format value
  const displayValue = format
    ? format(value)
    : precision !== undefined
    ? value.toFixed(precision)
    : value.toString();

  // Format percentage
  const displayPercentage =
    percentage !== null && showPercentage
      ? `${percentage >= 0 ? '+' : ''}${percentage.toFixed(precision)}%`
      : '';

  const textColor = getTextColor();

  // Compact variant - minimal indicator
  if (variant === 'compact') {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.2 }}
      >
        <Box
          className={className}
          sx={{
            display: 'inline-flex',
            alignItems: 'center',
            gap: 0.5,
            color: textColor,
            ...sx,
          }}
        >
          {getCompactIcon()}
          <Typography variant={TYPOGRAPHY_VARIANT_MAP[size]} sx={{ fontWeight: 600 }}>
            {displayPercentage || displayValue}
          </Typography>
        </Box>
      </motion.div>
    );
  }

  // Chip variant
  if (variant === 'chip') {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.2 }}
      >
        <Chip
          className={className}
          icon={getIcon()}
          label={
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
              <Typography variant={TYPOGRAPHY_VARIANT_MAP[size]} sx={{ fontWeight: 600 }}>
                {displayValue}
              </Typography>
              {displayPercentage && (
                <Typography variant={TYPOGRAPHY_VARIANT_MAP[size]}>
                  ({displayPercentage})
                </Typography>
              )}
              {label && (
                <Typography variant={TYPOGRAPHY_VARIANT_MAP[size]} color="text.secondary">
                  {label}
                </Typography>
              )}
            </Box>
          }
          sx={{
            backgroundColor: getChipColor(),
            color: textColor,
            fontWeight: 500,
            ...sx,
          }}
          size={size === 'small' ? 'small' : 'medium'}
        />
      </motion.div>
    );
  }

  // Default variant
  return (
    <motion.div
      initial={{ opacity: 0, x: -5 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{ duration: 0.3 }}
    >
      <Box
        className={className}
        sx={{
          display: 'flex',
          alignItems: 'center',
          gap: 0.75,
          ...sx,
        }}
      >
        {getIcon()}
        <Box sx={{ display: 'flex', flexDirection: 'column' }}>
          <Box sx={{ display: 'flex', alignItems: 'baseline', gap: 0.5 }}>
            <Typography
              variant={TYPOGRAPHY_VARIANT_MAP[size]}
              sx={{ fontWeight: 600, color: textColor }}
            >
              {displayValue}
              {displayPercentage && ` ${displayPercentage}`}
            </Typography>
          </Box>
          {label && (
            <Typography variant="caption" color="text.secondary">
              {label}
            </Typography>
          )}
        </Box>
      </Box>
    </motion.div>
  );
}

TrendIndicator.displayName = 'TrendIndicator';
