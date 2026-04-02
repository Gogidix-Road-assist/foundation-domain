import React from 'react';
import { FormGroup, FormLabel, FormHelperText, Box, BoxProps } from '@mui/material';
import { FormControlLabel } from '@mui/material';

export interface FormGroupProps extends BoxProps {
  label?: string;
  error?: string;
  required?: boolean;
}

/**
 * FormGroup Component
 * Enterprise-grade form group wrapper
 */
export const FormGroup: React.FC<FormGroupProps> = ({ label, error, required = false, children, ...props }) => {
  return (
    <Box {...props}>
      {label && (
        <FormLabel
          component="legend"
          sx={{
            color: '#374151',
            fontWeight: 500,
            marginBottom: '8px',
          }}
        >
          {label} {required && ' *'}
        </FormLabel>
      )}
      <FormGroup>{children}</FormGroup>
      {error && (
        <FormHelperText
          error
          sx={{
            marginTop: '4px',
            ml: '14px',
          }}
        >
          {error}
        </FormHelperText>
      )}
    </Box>
  );
};
