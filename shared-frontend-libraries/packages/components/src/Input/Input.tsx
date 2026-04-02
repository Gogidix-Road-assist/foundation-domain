import React from 'react';
import { motion } from 'framer-motion';
import {
  TextField as MuiTextField,
  TextFieldProps as MuiTextFieldProps,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export type InputSize = 'small' | 'medium' | 'large';
export type InputVariant = 'outlined' | 'filled' | 'standard';

export interface InputProps extends Omit<MuiTextFieldProps, 'size' | 'variant'> {
  size?: InputSize;
  variant?: InputVariant;
  helperText?: string;
  error?: boolean;
}

/**
 * Input Component
 * Enterprise-grade input with variants, sizes, and validation states
 */
export const Input = React.forwardRef<HTMLInputElement, InputProps>(
  (
    {
      size = 'medium',
      variant = 'outlined',
      helperText,
      error = false,
      disabled = false,
      className,
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
        <MuiTextField
          ref={ref as any}
          size={size}
          variant={variant}
          error={error}
          disabled={disabled}
          helperText={helperText}
          className={className}
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
            '& .MuiInputLabel-root': {
              color: '#6B7280',
              '&.Mui-focused': {
                color: error ? MANAGEMENT_COLORS.error : MANAGEMENT_COLORS.primary,
              },
            },
          }}
          {...props}
        />
      </motion.div>
    );
  }
);

Input.displayName = 'Input';
