import React from 'react';
import {
  Checkbox as MuiCheckbox,
  CheckboxProps as MuiCheckboxProps,
  FormControlLabel,
  Box,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface CheckboxProps extends Omit<MuiCheckboxProps, 'size'> {
  label?: string;
  error?: boolean;
  helperText?: string;
  size?: 'small' | 'medium' | 'large';
}

/**
 * FormCheckbox Component
 * Enterprise-grade checkbox for forms
 */
export const FormCheckbox = React.forwardRef<HTMLInputElement, CheckboxProps>(
  ({ label, error, helperText, size = 'medium', ...props }, ref) => {
  return (
    <Box sx={{ display: 'flex', alignItems: size === 'small' ? 'flex-start' : 'center' }}>
      <MuiCheckbox
        ref={ref}
        size={size}
        sx={{
          color: error ? MANAGEMENT_COLORS.error : '#374151',
        }}
        {...props}
      />
      {(label || helperText) && (
        <Box sx={{ ml: 2, flex: 1 }}>
          {label && (
            <FormControlLabel
              sx={{
                color: error ? MANAGEMENT_COLORS.error : '#374151',
                fontSize: size === 'small' ? '0.875rem' : '1rem',
              }}
            >
              {label}
            </FormControlLabel>
          )}
          {helperText && (
            <Box
              component="span"
              sx={{
                color: error ? MANAGEMENT_COLORS.error : '#6B7280',
                fontSize: '0.75rem',
                mt: 0.25,
              }}
            >
              {helperText}
            </Box>
          )}
        </Box>
      )}
    </Box>
  );
};

FormCheckbox.displayName = 'FormCheckbox';
