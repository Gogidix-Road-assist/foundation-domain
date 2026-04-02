import React from 'react';
import { Box, BoxProps } from '@mui/material';

export interface FormProps extends Omit<BoxProps, 'onSubmit'> {
  onSubmit?: (e: React.FormEvent<HTMLFormElement>) => void;
  noValidate?: boolean;
}

/**
 * Form Component
 * Enterprise-grade form wrapper
 */
export const Form: React.FC<FormProps> = ({ children, onSubmit, noValidate = false, ...props }) => {
  return (
    <Box
      component="form"
      onSubmit={onSubmit}
      noValidate={noValidate}
      sx={{
        width: '100%',
        display: 'flex',
        flexDirection: 'column',
        gap: 2,
      }}
      {...props}
    >
      {children}
    </Box>
  );
};
