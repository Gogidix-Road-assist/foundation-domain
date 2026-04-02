import React from 'react';
import { motion } from 'framer-motion';
import {
  Slider as MuiSlider,
  SliderProps as MuiSliderProps,
  Box,
  Typography,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type SliderColor = 'primary' | 'secondary' | 'error';
export type SliderSize = 'small' | 'medium';

export interface SliderProps extends Omit<MuiSliderProps, 'color' | 'size'> {
  color?: SliderColor;
  size?: SliderSize;
  label?: string;
  showValue?: boolean;
  valueLabel?: string;
}

const colorMap: Record<SliderColor, string> = {
  primary: MANAGEMENT_COLORS.primary,
  secondary: MANAGEMENT_COLORS.secondary,
  error: MANAGEMENT_COLORS.error,
};

/**
 * Slider Component
 * Enterprise-grade range slider with variants and value display
 */
export const Slider = React.forwardRef<HTMLDivElement, SliderProps>(
  (
    {
      color = 'primary',
      size = 'medium',
      label,
      showValue = false,
      valueLabel,
      value,
      onChange,
      disabled = false,
      min = 0,
      max = 100,
      step = 1,
      marks,
      className,
      ...props
    },
    ref
  ) => {
    const displayValue = Array.isArray(value) ? value[0] : value || min;
    const displayedLabel = valueLabel || `${displayValue}`;

    return (
      <motion.div
        ref={ref}
        className={className}
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.2 }}
      >
        {label && (
          <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
            <Typography variant="body2" sx={{ color: '#374151', fontWeight: 500 }}>
              {label}
            </Typography>
            {showValue && (
              <Typography variant="body2" sx={{ color: '#6B7280' }}>
                {displayedLabel}
              </Typography>
            )}
          </Box>
        )}
        <MuiSlider
          value={value}
          onChange={onChange}
          disabled={disabled}
          min={min}
          max={max}
          step={step}
          marks={marks}
          size={size}
          sx={{
            color: colorMap[color],
            '& .MuiSlider-thumb': {
              '&:hover': {
                boxShadow: `0 0 0 8px ${colorMap[color]}33`,
              },
            },
            '& .Mui-disabled': {
              opacity: 0.5,
            },
          }}
          {...props}
        />
      </motion.div>
    );
  }
);

Slider.displayName = 'Slider';
