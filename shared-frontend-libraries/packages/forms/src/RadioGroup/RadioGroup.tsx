import React from 'react';
import {
  RadioGroup as MuiRadioGroup,
  RadioGroupProps as MuiRadioGroupProps,
  FormControlLabel,
  FormHelperText,
  Box,
} from '@mui/material';
import { MANAGEMENT_COLORS } from '@shared-frontend-libraries/design-system';

export interface RadioOption {
  value: string;
  label: string;
}

export interface RadioGroupProps extends Omit<MuiRadioGroupProps, 'size'> {
  label?: string;
  error?: string;
  helperText?: string;
  options: RadioOption[];
  required?: boolean;
  size?: 'small' | 'medium' | 'large';
}

/**
 * FormRadioGroup Component
 * Enterprise-grade radio group for forms
 */
export const FormRadioGroup: React.FC<RadioGroupProps> = ({
  label,
  error,
  helperText,
  options,
  required = false,
  size = 'medium',
  ...props
}) => {
  return (
    <Box>
      {label && (
        <FormControlLabel
          component="legend"
          sx={{
            color: error ? MANAGEMENT_COLORS.error : '#374151',
            fontWeight: 500,
            marginBottom: '8px',
          }}
        >
          {label} {required && ' *'}
        </FormControlLabel>
      )}
      <MuiRadioGroup
        {...props}
        sx={{
          '& .MuiRadio-root': {
            padding: size === 'small' ? '4px' : size === 'medium' ? '6px' : '8px',
          },
        }}
      >
        {options.map((option) => (
          <Box key={option.value} sx={{ display: 'flex', alignItems: size === 'small' ? 'flex-start' : 'center', mb: 1 }}>
            <input type="radio" name="radiogroup" value={option.value} id={option.value} />
            <FormControlLabel
              htmlFor={option.value}
              sx={{
                color: error ? MANAGEMENT_COLORS.error : '#374151',
                fontSize: size === 'small' ? '0.875rem' : '1rem',
                ml: 1,
                cursor: 'pointer',
              }}
            >
              {option.label}
            </FormControlLabel>
          </Box>
        ))}
      </MuiRadioGroup>
      {error && (
        <FormHelperText
          error
          sx={{
            ml: '14px',
            mt: 1,
          }}
        >
          {error}
        </FormHelperText>
      )}
    </Box>
  );
};
