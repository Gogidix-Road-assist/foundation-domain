import React from 'react';
import { motion } from 'framer-motion';
import {
  Select as MuiSelect,
  SelectProps as MuiSelectProps,
  MenuItem,
  FormControl,
  InputLabel,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type SelectSize = 'small' | 'medium';
export type SelectVariant = 'outlined' | 'filled' | 'standard';

export interface SelectOption {
  value: string;
  label: string;
  disabled?: boolean;
}

export interface SelectProps extends Omit<MuiSelectProps, 'size' | 'variant'> {
  size?: SelectSize;
  variant?: SelectVariant;
  options?: SelectOption[];
  label?: string;
  error?: boolean;
  helperText?: string;
}

/**
 * Select Component
 * Enterprise-grade select dropdown with variants and validation states
 */
export const Select = React.forwardRef<HTMLDivElement, SelectProps>(
  (
    {
      size = 'medium',
      variant = 'outlined',
      options = [],
      label,
      error = false,
      helperText,
      disabled = false,
      className,
      children,
      ...props
    },
    ref
  ) => {
    return (
      <motion.div
        initial={{ opacity: 0, y: -10 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.2 }}
      >
        <FormControl
          fullWidth
          size={size}
          disabled={disabled}
          error={error}
          className={className}
          ref={ref as any}
          sx={{
            '& .MuiOutlinedInput-root': {
              borderRadius: '8px',
              '& fieldset': {
                borderColor: error ? MANAGEMENT_COLORS.error : '#E5E7EB',
              },
              '&:hover fieldset': {
                borderColor: error ? MANAGEMENT_COLORS.error : MANAGEMENT_COLORS.primary,
              },
              '&.Mui-focused fieldset': {
                borderColor: error ? MANAGEMENT_COLORS.error : MANAGEMENT_COLORS.primary,
              },
            },
          }}
        >
          {label && <InputLabel>{label}</InputLabel>}
          <MuiSelect
            variant={variant}
            {...props}
          >
            {children || options.map((option) => (
              <MenuItem
                key={option.value}
                value={option.value}
                disabled={option.disabled}
              >
                {option.label}
              </MenuItem>
            ))}
          </MuiSelect>
        </FormControl>
      </motion.div>
    );
  }
);

Select.displayName = 'Select';
