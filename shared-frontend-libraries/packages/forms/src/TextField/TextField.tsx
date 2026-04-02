import React from 'react';
import {
  TextField as MuiTextField,
  TextFieldProps as MuiTextFieldProps,
} from '@mui/material';
import { Input } from '@shared-frontend-libraries/components';

export interface TextFieldProps extends Omit<MuiTextFieldProps, 'size' | 'variant'> {
  label?: string;
  helperText?: string;
  error?: boolean;
  size?: 'small' | 'medium' | 'large';
}

/**
 * TextField Component
 * Enterprise-grade text input field
 */
export const TextField = React.forwardRef<HTMLInputElement, TextFieldProps>(
  ({ label, helperText, error, size = 'medium', ...props }, ref) => {
    return (
      <MuiTextField
        ref={ref}
        label={label}
        helperText={helperText}
        error={error}
        size={size}
        variant="outlined"
        fullWidth
        {...props}
      />
    );
  }
);

TextField.displayName = 'TextField';
