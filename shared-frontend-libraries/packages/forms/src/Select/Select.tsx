import React from 'react';
import {
  Select as MuiSelect,
  SelectProps as MuiSelectProps,
  MenuItem,
  FormControl,
  InputLabel,
  FormHelperText,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface SelectOption {
  value: string;
  label: string;
}

export interface SelectProps extends Omit<MuiSelectProps, 'size'> {
  label?: string;
  helperText?: string;
  error?: boolean;
  required?: boolean;
  options: SelectOption[];
  size?: 'small' | 'medium' | 'large';
}

/**
 * FormSelect Component
 * Enterprise-grade dropdown select for forms
 */
export const FormSelect = React.forwardRef<HTMLDivElement, SelectProps>(
  ({ label, helperText, error, required = false, options, size = 'medium', ...props }, ref) => {
  return (
    <FormControl fullWidth error={error} ref={ref as any} size={size}>
      {label && (
        <InputLabel
          sx={{
            color: error ? MANAGEMENT_COLORS.error : '#374151',
            fontWeight: 500,
          }}
        >
          {label} {required && ' *'}
        </InputLabel>
      )}
      <MuiSelect
        label={label}
        error={error}
        size={size}
        {...props}
      >
        {options.map((option) => (
          <MenuItem key={option.value} value={option.value}>
            {option.label}
          </MenuItem>
        ))}
      </MuiSelect>
      {helperText && (
        <FormHelperText error={error}>{helperText}</FormHelperText>
      )}
    </FormControl>
  );
};

FormSelect.displayName = 'FormSelect';
